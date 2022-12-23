package com.example.testcontainers.consumer.config.kafka;

import java.util.concurrent.CountDownLatch;

import com.example.testcontainers.consumer.dao.EmployeeRepository;
import com.example.testcontainers.consumer.dao.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {

    @Autowired
    KafkaProducer kafkaProducer;


    private CountDownLatch latch = new CountDownLatch(1);


    @KafkaListener(topics =  "${spring.kafka.consumer.topic}", groupId =  "${spring.kafka.consumer.group}")
    public void receive(String message) {

        log.debug("received payload='{}'",message);

        kafkaProducer.sendMessage(message.toUpperCase(), "data-consumer-out");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

}