package com.divary.rabbitmqconsumer.repository;

import com.divary.rabbitmqconsumer.entity.SimpleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleRepository extends JpaRepository<SimpleEntity, String> {

}
