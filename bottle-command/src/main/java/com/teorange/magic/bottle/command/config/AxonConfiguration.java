//package com.teorange.magic.bottle.command.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.ExchangeBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//@Slf4j
//public class AxonConfiguration {
//
//  @Value("${axon.amqp.exchange}")
//  private String exchangeName;
//
//
//  @Bean
//  public Exchange exchange() {
//    return ExchangeBuilder.fanoutExchange(exchangeName).durable(true).build();
//  }
//
//
//  @Bean
//  public Queue queue() {
//    return new Queue("magic-bottle-queue", true);
//  }
//
//  @Bean
//  public Binding queueBinding() {
//    return BindingBuilder.bind(queue()).to(exchange()).with("").noargs();
//  }
//
//
//}
