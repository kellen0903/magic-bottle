//package com.teorange.magic.bottle.admin.config;
//
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
//import org.axonframework.serialization.Serializer;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.ExchangeBuilder;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
//  @Bean
//  public Exchange exchange(){
//    return ExchangeBuilder.fanoutExchange(exchangeName).durable(true).build();
//  }
//
//
//  @Bean
//  public Queue queue(){
//    return new Queue("magic-bottle-queue", true);
//  }
//
//  @Bean
//  public Binding queueBinding() {
//    return BindingBuilder.bind(queue()).to(exchange()).with("").noargs();
//  }
//
//
//
//
//  // listen to queue
//  @Bean
//  public SpringAMQPMessageSource queueMessageSource(Serializer serializer) {
//    return new SpringAMQPMessageSource(serializer) {
//      @RabbitListener(queues = "magic-bottle-queue")
//      @Override
//      public void onMessage(Message message, Channel channel) throws Exception {
//        log.info("message received: {}", message.toString());
//        super.onMessage(message, channel);
//      }
//    };
//  }
//}
