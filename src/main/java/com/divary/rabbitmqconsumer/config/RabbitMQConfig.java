package com.divary.rabbitmqconsumer.config;

import com.divary.rabbitmqconsumer.preference.ConfigPreference;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    private final ConfigPreference preference;

    private static final String DEATH_LATTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String DEATH_LATTER_ROUTING_KEY = "x-dead-letter-routing-key";
    private static final String MESSAGE_TTL = "x-message-ttl";

    @Autowired
    public RabbitMQConfig(ConfigPreference preference) {
        this.preference = preference;
    }

//    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(preference.rabbitmqHost);
        cachingConnectionFactory.setPort(preference.rabbitmqPort);
        cachingConnectionFactory.setUsername(preference.rabbitmqUsername);
        cachingConnectionFactory.setPassword(preference.rabbitmqPassword);

        return cachingConnectionFactory;

    }

    @Bean
    public RabbitTemplate rabbitTemplate(){

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        return rabbitTemplate;
    }

    /*
    DIRECT
     */

    /*
    Create Exchange in RabbitMq
    */
    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(preference.directExchange);
    }

    /*
    Create Queue in RabbitMq
     */
    @Bean
    Queue directStringQueue(){
        return QueueBuilder.durable(preference.directStringQueue).build();
    }

    @Bean
    Queue directObjectQueue(){
        return QueueBuilder.durable(preference.directObjectQueue).build();
    }

    /*
    Create Binding in RabbitMq
    */

    @Bean
    Binding directStringBinding(){
        return BindingBuilder.bind(directStringQueue()).to(directExchange()).with(preference.directStringRoutingKey);
    }

    @Bean
    Binding directObjectBinding(){
        return BindingBuilder.bind(directObjectQueue()).to(directExchange()).with(preference.directObjectRoutingKey);
    }

    /*
    FANOUT
     */

    /*
    Create Exchange in Rabbit Mq
     */
    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(preference.fanoutExchange);
    }

    /*
    Create Queue in RabbitMq
     */
    @Bean
    Queue fanoutStringQueue(){
        return QueueBuilder.durable(preference.fanoutStringQueue).build();
    }

    @Bean
    Queue fanoutObjectQueue(){
        return QueueBuilder.durable(preference.fanoutObjectQueue).build();
    }

    /*
    Create Binding in Rabbit Mq
     */
    @Bean
    Binding fanoutStringBinding(){
        return BindingBuilder.bind(fanoutStringQueue()).to(fanoutExchange());
    }

    @Bean
    Binding fanoutObjectBinding(){
        return BindingBuilder.bind(fanoutObjectQueue()).to(fanoutExchange());
    }

    /*
    DEAD
     */

    /*
    Create Exchange in Rabbit Mq
     */
    @Bean
    FanoutExchange deadFanoutExchange(){
        return new FanoutExchange(preference.deadFanoutExchange);
    }

    @Bean
    DirectExchange deadRetryExchange(){
        return new DirectExchange(preference.deadRetryExchange);
    }

    @Bean
    DirectExchange deadFinalExchange(){
        return new DirectExchange(preference.deadFinalExchange);
    }

    /*
    Create Queue in Rabbit Mq
     */
    @Bean
    Queue deadOriginalQueue(){
        return QueueBuilder.durable(preference.deadOriginalQueue)
                .withArgument(DEATH_LATTER_EXCHANGE, preference.deadRetryExchange)
                .withArgument(DEATH_LATTER_ROUTING_KEY, preference.deadRetryRoutingKey)
                .withArgument(MESSAGE_TTL, 3000).build();
    }

    @Bean
    Queue deadRetryQueue(){
        return QueueBuilder.durable(preference.deadRetryQueue)
                .withArgument(DEATH_LATTER_EXCHANGE, preference.deadRetryExchange)
                .withArgument(DEATH_LATTER_ROUTING_KEY, preference.deadOriginalRoutingKey)
                .withArgument(MESSAGE_TTL, 5000).build();
    }

    @Bean
    Queue deadFinalQueue(){
        return QueueBuilder.durable(preference.deadFinalQueue).build();
    }

    /*
    Create Binding in Rabbit Mq
     */
    @Bean
    Binding deadOriginalToFanoutBinding(){
        return BindingBuilder.bind(deadOriginalQueue()).to(deadFanoutExchange());
    }

    @Bean
    Binding deadOriginalBinding(){
        return BindingBuilder.bind(deadOriginalQueue()).to(deadRetryExchange()).with(preference.deadOriginalRoutingKey);
    }

    @Bean
    Binding deadRetryBinding(){
        return BindingBuilder.bind(deadRetryQueue()).to(deadRetryExchange()).with(preference.deadRetryRoutingKey);
    }

    @Bean
    Binding deadFinalBinding(){
        return BindingBuilder.bind(deadFinalQueue()).to(deadFinalExchange()).with(preference.deadFinalRoutingKey);
    }
}
