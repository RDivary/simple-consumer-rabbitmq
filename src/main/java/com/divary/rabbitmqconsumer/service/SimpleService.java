package com.divary.rabbitmqconsumer.service;

import com.divary.rabbitmqconsumer.entity.SimpleEntity;
import com.divary.rabbitmqconsumer.repository.SimpleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SimpleService {

    private static final Logger logger = LogManager.getLogger();

    SimpleRepository simpleRepository;

    public SimpleService(SimpleRepository simpleRepository) {
        this.simpleRepository = simpleRepository;
    }

    public void saveDB(String message){

        logger.info("Message = {}", message);

        SimpleEntity entity = new SimpleEntity();
        entity.setMessage(message);

        simpleRepository.save(entity);

    }

}
