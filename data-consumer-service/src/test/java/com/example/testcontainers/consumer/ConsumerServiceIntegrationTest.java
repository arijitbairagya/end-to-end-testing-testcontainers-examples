package com.example.testcontainers.consumer;

import com.example.testcontainers.consumer.dao.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = ConsumerServiceIntegrationTest.class)
@ActiveProfiles("integration-test")
public class ConsumerServiceIntegrationTest extends AbstractIntegrationTest {


    // load application using generic docker container
    private static GenericContainer dataConsumerContainer = new GenericContainer(DockerImageName.parse("data-consumer-service:0.0.1-SNAPSHOT"));

    @BeforeAll
    public static void  setupContainers() {
        // call
        initContainers();

        dataConsumerContainer
                .dependsOn(kafkaContainer, postgreSQLContainer)
                .withNetwork(dockerNetwork)
                .withNetworkAliases("data-consumer-service")
                .withEnv(
                        Map.of("DATABASE_URL" ,  "jdbc:postgresql://host.docker.internal:" + postgreSQLContainer.getMappedPort(5432)+ "/"+ postgreSQLContainer.getDatabaseName(),
                                "DATABASE_USER", postgreSQLContainer.getUsername(),
                                "DATABASE_PASSWORD", postgreSQLContainer.getPassword(),
                                "BOOTSTRAP_SERVER", "host.docker.internal:"+ kafkaContainer.getFirstMappedPort()))
                .withExposedPorts(8081)
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("ConsumerService"))
                .waitingFor(Wait.forLogMessage(".*Started DataConsumerService.*\\n", 1))
                .withReuse(false);

        // start SUT generic container
        log.debug("Starting data consumer service container..");
        dataConsumerContainer.start();
    }

    @Test
    public void testKafkaConsumer() {
        log.debug("Kafka address:{}", kafkaContainer.getBootstrapServers());
    }

    @Test
    void testRestEndpoint() {
        log.debug("Test Rest API Call ..");
    }
}
