package com.divary.rabbitmqconsumer.event.ampq;

import com.divary.rabbitmqconsumer.dto.Request;
import com.divary.rabbitmqconsumer.preference.ConfigPreference;
import com.divary.rabbitmqconsumer.service.SimpleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RabbitListenerConfig {

    private static final Logger logger = LogManager.getLogger();

    private SimpleService simpleService;
    private RabbitTemplate rabbitTemplate;
    private ConfigPreference preference;

    private static final String DEATH_LETTER_HEADER = "x-death";
    private static final String EXCHANGE = "exchange";
    private static final String ROUTING_KEYS = "routing-keys";
    private static final String REASON = "reason";
    private static final String COUNT = "count";

    private static final String REASON_REJECTED = "rejected";
    private static final String REASON_EXPIRED = "expired";

    @Autowired
    public RabbitListenerConfig(SimpleService simpleService, RabbitTemplate rabbitTemplate, ConfigPreference preference) {
        this.simpleService = simpleService;
        this.rabbitTemplate = rabbitTemplate;
        this.preference = preference;
    }

    /*
    Direct Exchange
     */
    @RabbitListener(queues = "${direct.string.queue}")
    public void consumeDirectString1(String message){

        logger.info("Message direct string from RabbitMq1 = {}", message);

        simpleService.saveDB(message);
    }

    @RabbitListener(queues = "${direct.string.queue}")
    public void consumeDirectString2(String message){

        logger.info("Message direct string from RabbitMq2 ={}", message);

        simpleService.saveDB(message);
    }

    @RabbitListener(queues = "${direct.object.queue}")
    public void consumeDirectObject1(Message message){

        try {

            Request request = new ObjectMapper().readValue(message.getBody(), Request.class);

            logger.info("Message direct object from new RabbitMq1 = {}", request.getMessage());

            simpleService.saveDB(request.getMessage());

        } catch (Exception err) {
            logger.error("Error parse rabbit message : {}", ExceptionUtils.getStackTrace(err));
        }

    }

    @RabbitListener(queues = "${direct.object.queue}")
    public void consumeDirectObject2(Message message){

        try {

            Request request = new ObjectMapper().readValue(message.getBody(), Request.class);

            logger.info("Message direct object from new RabbitMq2 = {}", request.getMessage());

            simpleService.saveDB(request.getMessage());

        } catch (Exception err) {
            logger.error("Error parse rabbit message : {}", ExceptionUtils.getStackTrace(err));
        }

    }

    /*
    Fanout Exchange
     */
    @RabbitListener(queues = "${fanout.string.queue}")
    public void consumeFanoutString1(String message){

        logger.info("Message fanout string from RabbitMq1 = {}", message);

        simpleService.saveDB(message);
    }

    @RabbitListener(queues = "${fanout.string.queue}")
    public void consumeFanoutString2(String message){

        logger.info("Message fanout string from RabbitMq2 ={}", message);

        simpleService.saveDB(message);
    }

    @RabbitListener(queues = "${fanout.object.queue}")
    public void consumeFanoutObject1(Message message){

        try {

            Request request = new ObjectMapper().readValue(message.getBody(), Request.class);

            logger.info("Message fanout object from new RabbitMq1 = {}", request.getMessage());

            simpleService.saveDB(request.getMessage());

        } catch (Exception err) {
            logger.error("Error parse rabbit message : {}", err.getMessage());
        }

    }

    @RabbitListener(queues = "${fanout.object.queue}")
    public void consumeFanoutObject2(Message message){

        try {

            Request request = new ObjectMapper().readValue(message.getBody(), Request.class);

            logger.info("Message fanout object from new RabbitMq2 = {}", request.getMessage());

            simpleService.saveDB(request.getMessage());

        } catch (Exception err) {
            logger.error("Error parse rabbit message : {}", ExceptionUtils.getStackTrace(err));
        }

    }

    /*
    Dead Exchange
     */

    @RabbitListener(queues = "${dead.original.queue}")
    public void consumeDead(Message message){

        try {

            if (isStillAvailableConsume(message)) {

                Request request = new ObjectMapper().readValue(message.getBody(), Request.class);

                logger.info("Message dead from new RabbitMq = {}", request.getMessage());

            } else {

                rabbitTemplate.convertAndSend(preference.deadFinalExchange, preference.deadFinalRoutingKey, message);
                logger.info("Send message to parking lot = {}", message);

            }

        } catch (Exception err) {

            logger.error("Error parse rabbit message for profile picture or POLO: {}", ExceptionUtils.getStackTrace(err));
            throw new AmqpRejectAndDontRequeueException("Error parse rabbit message for profile picture or POLO");

        }

    }

    private boolean isStillAvailableConsume(Message payload) {

        if (payload.getMessageProperties().getHeaders() == null){
            return true;
        }
        try {
            List<HashMap> header = (ArrayList) payload.getMessageProperties().getHeaders().get(DEATH_LETTER_HEADER);
            List<HashMap> filterHeader = header
                    .stream()
                    .filter(s -> (s.get(EXCHANGE).equals(preference.deadRetryExchange) && ((ArrayList) s.get(ROUTING_KEYS)).get(0).equals(preference.deadRetryRoutingKey) ||
                            s.get(EXCHANGE).equals(preference.deadFanoutExchange))
                            && (s.get(REASON).equals(REASON_EXPIRED) || s.get(REASON).equals(REASON_REJECTED)))
                    .collect(Collectors.toList());

            List<HashMap> filterRejected = filterHeader
                    .stream()
                    .filter(s -> s.get(REASON).equals(REASON_REJECTED))
                    .collect(Collectors.toList());
            Long rejectedCount = !filterRejected.isEmpty() ? (Long) filterRejected.get(0).get(COUNT) : null;

            List<HashMap> filterExpired = filterHeader
                    .stream()
                    .filter(s -> s.get(REASON).equals(REASON_EXPIRED))
                    .collect(Collectors.toList());
            Long expiredCount = !filterExpired.isEmpty() ? (Long) filterExpired.get(0).get(COUNT) : null;

            return (rejectedCount != null && rejectedCount < 3) || (expiredCount != null && expiredCount < 3);
        } catch (Exception ex) {
            return true;
        }
    }
}
