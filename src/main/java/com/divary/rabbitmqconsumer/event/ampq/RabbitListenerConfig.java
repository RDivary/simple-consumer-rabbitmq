package com.divary.rabbitmqconsumer.event.ampq;

import com.divary.rabbitmqconsumer.service.SimpleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitListenerConfig {

    private static final Logger logger = LogManager.getLogger();

    SimpleService simpleService;

    @Autowired
    public RabbitListenerConfig(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    @RabbitListener(queues = "${simple.queue}")
    public void consume(String message){

        logger.info("Message from RabbitMq ={}", message);

        simpleService.saveDB(message);
    }

}
