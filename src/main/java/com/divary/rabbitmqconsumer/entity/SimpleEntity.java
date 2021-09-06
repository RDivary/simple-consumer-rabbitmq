package com.divary.rabbitmqconsumer.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "simple_entity")
public class SimpleEntity {

    @Id
    @GeneratedValue(generator = "car_uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "car_uuid", strategy = "uuid")
    private String id;

    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
