package com.divary.rabbitmqconsumer.preference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigPreference {

    @Value("${spring.rabbitmq.host}")
    public String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    public int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    public String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    public String rabbitmqPassword;

    //DIRECT

    @Value("${direct.exchange}")
    public String directExchange;

    @Value("${direct.string.routingkey}")
    public String directStringRoutingKey;

    @Value("${direct.object.routingkey}")
    public String directObjectRoutingKey;

    @Value("${direct.string.queue}")
    public String directStringQueue;

    @Value("${direct.object.queue}")
    public String directObjectQueue;

    //FANOUT

    @Value("${fanout.exchange}")
    public String fanoutExchange;

    @Value("${fanout.string.queue}")
    public String fanoutStringQueue;

    @Value("${fanout.object.queue}")
    public String fanoutObjectQueue;

    // DEAD

    @Value("${dead.fanout.exchange}")
    public String deadFanoutExchange;

    @Value("${dead.retry.exchange}")
    public String deadRetryExchange;

    @Value("${dead.final.exchange}")
    public String deadFinalExchange;

    @Value("${dead.original.routingkey}")
    public String deadOriginalRoutingKey;

    @Value("${dead.retry.routingkey}")
    public String deadRetryRoutingKey;

    @Value("${dead.final.routingkey}")
    public String deadFinalRoutingKey;

    @Value("${dead.original.queue}")
    public String deadOriginalQueue;

    @Value("${dead.retry.queue}")
    public String deadRetryQueue;

    @Value("${dead.final.queue}")
    public String deadFinalQueue;

}
