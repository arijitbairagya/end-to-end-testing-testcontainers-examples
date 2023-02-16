package com.example.testcontainers.it;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = ConsumerServiceDynamicPropertyLoadingIntegrationTest.class)
@ActiveProfiles("integration-test")
public class ConsumerServiceDynamicPropertyLoadingIntegrationTest extends AbstractIntegrationTest {


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
                                "BOOTSTRAP_SERVER", "host.docker.internal:"+kafkaContainer.getFirstMappedPort()))
                .withExposedPorts(8080)
                .withFileSystemBind(Paths.get("src/test/resources/application-integration-test.yml").toAbsolutePath().toString(),
                        "/application.yml", BindMode.READ_ONLY)
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("ConsumerService"))
                .waitingFor(Wait.forLogMessage(".*Started DataConsumerService.*\\n", 1))
                .withReuse(false);

        // start SUT generic container
        log.debug("Starting data consumer service container..");
        dataConsumerContainer.start();
    }

    @Test
    public void testKafkaIntegration() {
        log.debug("Kafka address:{}", kafkaContainer.getBootstrapServers());

        KafkaTemplate kafkaTemplate = getKafkaTemplate(kafkaContainer);

        String message = "Arijit";
        kafkaTemplate.send("consumer-test-topic", message);

        KafkaConsumer<String, String> kafkaConsumer = getConsumer(kafkaContainer);
        kafkaConsumer.subscribe(Collections.singletonList("data-consumer-out"));

        Awaitility.await().untilAsserted(() ->  {
            ConsumerRecords<String, String> record = kafkaConsumer.poll(Duration.ofMillis(1000));
            record.forEach( rec -> {
                log.debug("Got processed : {} for input message:: {}", message, rec.value());
                Assert.assertEquals("ARIJIT", rec.value());
            });
        });
    }

    private KafkaTemplate<String, String> getKafkaTemplate(KafkaContainer kafkaContainer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers());
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
      ProducerFactory producerFactory = new DefaultKafkaProducerFactory<>(configProps);

        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        return kafkaTemplate;
    }
    private KafkaConsumer<String, String> getConsumer(
            KafkaContainer kafkaContainer) {

        return new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest"),
                new StringDeserializer(),
                new StringDeserializer());
    }

    @Test
    void testRestEndpoint() {
        log.debug("Test Rest API Call ..");
    }
}
