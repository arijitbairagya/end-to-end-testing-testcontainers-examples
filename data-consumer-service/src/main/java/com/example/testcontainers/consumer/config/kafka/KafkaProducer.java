package com.example.testcontainers.consumer.config.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class KafkaProducer {

    @Autowired
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message, String topic) {
        log.debug("Publishing message to kafka via template topic::{} message::{}", topic, message);
        kafkaTemplate.send(topic, message);
//          .addCallback(
//            result -> log.info("Message sent to topic: {}", message),
//            ex -> log.error("Failed to send message", ex)
//          );
    }
}