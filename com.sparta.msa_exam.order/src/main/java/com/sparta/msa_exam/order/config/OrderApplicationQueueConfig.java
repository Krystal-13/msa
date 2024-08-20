package com.sparta.msa_exam.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderApplicationQueueConfig {

    @Value("${messaging.exchanges.default}")
    private String exchange;

    @Value("${messaging.queues.product}")
    private String queueProduct;

    @Value("${messaging.exchanges.error}")
    private String exchangeErr;

    @Value("${messaging.queues.orderError}")
    private String queueErrOrder;

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange exchange() { return new TopicExchange(exchange);}
    @Bean
    public Queue queueProduct() { return new Queue(queueProduct);}
    @Bean
    public Binding bindingProduct() {
        return BindingBuilder.bind(queueProduct()).to(exchange()).with(queueProduct);
    }
    @Bean
    public TopicExchange exchangeErr() { return new TopicExchange(exchangeErr);}
    @Bean
    public Queue queueErrOrder() { return new Queue(queueErrOrder);}
    @Bean
    public Binding bindingErrOrder() {
        return BindingBuilder.bind(queueErrOrder()).to(exchangeErr()).with(queueErrOrder);
    }


}
