package com.example.testcontainers.containers;

import com.example.testcontainers.DockerImages;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class CustomKafkaContainer extends KafkaContainer {

    private static CustomKafkaContainer kafkaContainer;

    private CustomKafkaContainer() {
        super(DockerImageName.parse(DockerImages.KAFKA_CONTAINER_IMAGE));
    }

    public static CustomKafkaContainer getInstance() {
        if(kafkaContainer == null)
            kafkaContainer = new CustomKafkaContainer();

        return kafkaContainer;
    }

    public void initialize(Network network) {
        log.debug("initialize kafka container..." );
        kafkaContainer
                .withNetwork(network)
                .withNetworkAliases("kafka")
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Kafka"))
                .withReuse(true);
    }

    @Override
    public void start() {
        log.debug("Setting system property kafka BOOTSTRAP_SERVER ::{}", kafkaContainer.getBootstrapServers());
        super.start();
        System.setProperty("BOOTSTRAP_SERVER", kafkaContainer.getBootstrapServers());
    }
}
