package com.sparta.msa_exam.payment.service;

import com.sparta.msa_exam.payment.dto.PaymentDeliveryMessage;
import com.sparta.msa_exam.payment.repository.PaymentRepository;
import com.sparta.msa_exam.payment.dto.PaymentRequest;
import com.sparta.msa_exam.payment.dto.PaymentResponse;
import com.sparta.msa_exam.payment.model.Payment;
import com.sparta.msa_exam.payment.type.ErrorType;
import com.sparta.msa_exam.payment.type.PayResult;
import com.sparta.msa_exam.payment.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${messaging.queues.order}")
    private String queueOrder;

    @Value("${messaging.queues.orderError}")
    private String paymentErrorQueue;

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        Payment payment = Payment.createPayment(
                paymentRequest.getUserId(),
                paymentRequest.getOrderId(),
                paymentRequest.getTotalPrice(),
                PaymentStatus.SUCCESS
        );

        if (PaymentStatus.FAILURE.equals(payment.getStatus())) {
            log.info(
                    "PayResult.FAIL : {" +
                    " userId : " + paymentRequest.getUserId() +
                    " orderId : " + paymentRequest.getOrderId() +
                    " }"
            );

            PaymentDeliveryMessage paymentDeliveryMessage = entityToMessage(payment);
            paymentDeliveryMessage.setErrorType(ErrorType.PAYMENT_FAIL.name());

            rabbitTemplate.convertAndSend(paymentErrorQueue, paymentDeliveryMessage);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        paymentRepository.save(payment);

        rabbitTemplate.convertAndSend(queueOrder, entityToMessage(payment));

        return new PaymentResponse(PayResult.SUCCESS.name());
    }

    private PaymentDeliveryMessage entityToMessage(Payment payment) {
        return PaymentDeliveryMessage.builder()
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .payResult(payment.getStatus().name())
                .build();
    }
}
