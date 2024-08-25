package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.repository.OrderItemRepository;
import com.sparta.msa_exam.order.repository.OrderRepository;
import com.sparta.msa_exam.order.model.Order;
import com.sparta.msa_exam.order.model.OrderItem;
import com.sparta.msa_exam.order.type.OrderStatus;
import com.sparta.msa_exam.order.dto.OrderDeliveryMessage;
import com.sparta.msa_exam.order.dto.OrderItemDto;
import com.sparta.msa_exam.order.dto.OrderResponse;
import com.sparta.msa_exam.payment.dto.PaymentDeliveryMessage;
import com.sparta.msa_exam.payment.type.PaymentStatus;
import com.sparta.msa_exam.product.dto.ProductItemDto;
import com.sparta.msa_exam.product.dto.ProductItemResponse;
import com.sparta.msa_exam.product.dto.ProductOrderValidationRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Value("${messaging.queues.product}")
    private String queueProduct;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 주문 생성 요청을 처리하는 메서드
     *
     * 이 메서드는 사용자의 주문 요청이 들어오면 다음의 작업을 수행합니다:
     * 1. 상품 서비스(ProductService)에 해당 상품들이 존재하는지 확인하고,
     *    각 상품의 재고가 충분히 있는지 검증합니다.
     * 2. 검증된 상품 정보를 바탕으로 총 주문 금액을 계산합니다.
     * 3. 사용자의 ID를 기반으로 주문(Order) 객체를 생성하고 저장합니다.
     * 4. 주문 항목(OrderItem)들을 생성하고, 이를 저장소에 저장합니다.
     * 5. 생성된 주문 항목들과 총 금액을 주문에 설정하고, 최종적으로 주문을 'PENDING' 상태로 준비합니다.
     * 6. 주문 정보를 OrderResponse 객체로 변환하여 반환합니다.
     *
     * @param requestOrderItems 사용자가 요청한 주문 항목 목록(OrderItemDto 리스트)
     * @param userId            주문을 생성하는 사용자의 ID
     * @return 생성된 주문 정보를 포함하는 OrderResponse 객체
     */
    @Transactional
    public OrderResponse createOrder(List<OrderItemDto> requestOrderItems, String userId) {

        List<ProductItemResponse> productItemResponse =
                validateAndFetchProductInfo(requestOrderItems);
        int totalPrice = calculateTotalPrice(productItemResponse, requestOrderItems);

        Order order = createAndSaveOrder(userId);
        List<OrderItem> orderItems = createOrderItems(order, requestOrderItems);
        orderItemRepository.saveAll(orderItems);
        order.setupOrder(orderItems, totalPrice);

        return orderToDto(order);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "order", key = "args[0]")
    public OrderResponse getOrder(Long orderId, String userId) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        return orderToDto(order);
    }

    @Transactional
    public OrderResponse updateOrder(
            Long orderId, List<OrderItemDto> requestOrderItems, String userId
    ) {

        Order order = getOrder(orderId);
        validateOrderUser(userId, order);

        List<ProductItemResponse> productItemResponse =
                validateAndFetchProductInfo(requestOrderItems);
        int totalPrice = calculateTotalPrice(productItemResponse, requestOrderItems);

        List<OrderItem> orderItems = createOrderItems(order, requestOrderItems);
        order.setupOrder(orderItems, totalPrice);

        return orderToDto(order);
    }

    /**
     * 결제 결과를 처리하는 메서드
     *
     * 이 메서드는 Payment 서비스로부터 결제 결과 메시지를 수신하여 다음의 작업을 수행합니다:
     * 1. 결제 결과 메시지에서 주문 ID를 사용해 해당 주문을 조회합니다.
     *    - 주문이 존재하지 않을 경우, 로그를 남기고 처리를 중단합니다.
     * 2. 결제가 실패한 경우, 로그를 남기고 주문 상태를 'CANCELLED'로 변경합니다.
     * 3. 결제가 성공한 경우, 주문 상태를 'CONFIRMED'로 변경하고,
     *    상품 재고를 감소시키기 위해 해당 주문 정보를 큐(queueProduct)에 전송합니다.
     *
     * @param paymentDeliveryMessage Payment 서비스로부터 수신된 결제 결과 메시지
     */
    @Transactional
    public void handleMessage(PaymentDeliveryMessage paymentDeliveryMessage) {

        Optional<Order> optionalOrder = orderRepository.findById(paymentDeliveryMessage.getOrderId());
        if (optionalOrder.isEmpty()) {
            log.info("orderID : " + paymentDeliveryMessage.getOrderId() + " NOT FOUND");
            return;
        }

        Order order = optionalOrder.get();
        if (PaymentStatus.FAILURE.name().equals(paymentDeliveryMessage.getPayResult())) {
            log.info("orderID : " + paymentDeliveryMessage.getOrderId() + " PAY FAILED");
            order.setOrderStatus(OrderStatus.CANCELLED);
            return;
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        rabbitTemplate.convertAndSend(queueProduct, entityToMessage(order));
    }

    @Transactional
    public void handleErrorMessage(OrderDeliveryMessage orderDeliveryMessage) {

        Optional<Order> optionalOrder = orderRepository.findById(orderDeliveryMessage.getOrderId());
        if (optionalOrder.isEmpty()) {
            log.info("orderId : " + orderDeliveryMessage.getOrderId() + " Not found");
            return;
        }

        Order order = optionalOrder.get();
        order.setOrderStatus(OrderStatus.CANCELLED);
    }

    private Order createAndSaveOrder(String userId) {
        Order order = Order.createOrder(userId, createOrderName());
        orderRepository.save(order);
        return order;
    }

    private List<OrderItem> createOrderItems(
            Order order,
            List<OrderItemDto> orderItems
    ) {
        return orderItems.stream()
                .map(item -> OrderItem.createOrderItem(
                        order,
                        item.getProductId(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    private List<ProductItemResponse> validateAndFetchProductInfo(List<OrderItemDto> orderItems) {

        try {
            return productClient.validateAndGetProductInfo(
                    new ProductOrderValidationRequest(convert(orderItems))
            );
        } catch (FeignException.NotFound | FeignException.BadRequest e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private int calculateTotalPrice(
            List<ProductItemResponse> productItemResponse,
            List<OrderItemDto> orderItems
    ) {
        Map<Long, Integer> orderItemMap = productItemResponse.stream()
                .collect(Collectors.toMap(
                        ProductItemResponse::getProductId,
                        ProductItemResponse::getSupplyPrice
                ));

        return orderItems.stream()
                .mapToInt(item ->
                        orderItemMap.get(item.getProductId()) * item.getQuantity())
                .sum();
    }

    private Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found or has been deleted"
                        ));
    }

    private static void validateOrderUser(String userId, Order order) {
        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized access to the order"
            );
        }
    }

    private String createOrderName() {

        int randomSixNumber = (int) (Math.random() * 899999) + 100000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());

        return today + randomSixNumber;
    }

    private List<ProductItemDto> convert(List<OrderItemDto> orderItems) {

        List<ProductItemDto> productItems = new ArrayList<>();
        for (OrderItemDto orderItem : orderItems) {
            productItems.add(
                    new ProductItemDto(
                            orderItem.getProductId(),
                            orderItem.getQuantity()
                    )
            );
        }

        return productItems;
    }

    private OrderResponse orderToDto(Order order) {
        return OrderResponse.builder()
                .name(order.getName())
                .productIds(order.getOrderItems().stream()
                        .map(OrderItem::getProductId).toList())
                .orderStatus(order.getOrderStatus().name())
                .build();
    }

    private OrderItemDto orderItemToDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .build();
    }

    private OrderDeliveryMessage entityToMessage(Order order) {
        List<OrderItemDto> orderItems =
                order.getOrderItems().stream().map(this::orderItemToDto).toList();

        return OrderDeliveryMessage.builder()
                .orderId(order.getId())
                .userId(Long.valueOf(order.getUserId()))
                .orderItems(orderItems)
                .build();
    }
}
