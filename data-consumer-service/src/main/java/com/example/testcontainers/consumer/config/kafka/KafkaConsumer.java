package com.example.testcontainers.consumer.config.kafka;

import java.util.concurrent.CountDownLatch;

import com.example.testcontainers.consumer.dao.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class KafkaConsumer {

    @Autowired
    EmployeeRepository employeeRepository;


    private CountDownLatch latch = new CountDownLatch(1);


    @KafkaListener(topics = "${app.kafka.consumer.topic}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        log.debug("received payload='{}'", consumerRecord.toString());


//        employeeRepository.save();
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

}