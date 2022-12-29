package com.example.testcontainers;

import com.example.testcontainers.config.model.Image;
import com.example.testcontainers.containers.CustomKafkaContainer;
import com.example.testcontainers.containers.CustomPostgreSQLContainer;
import com.example.testcontainers.containers.DataConsumerServiceContainer;
import com.example.testcontainers.properties.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.lang3.ObjectUtils;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Slf4j
@ContextConfiguration(classes = Application.class)
@SpringBootTest(classes = EndToEndTesting.class)
@ActiveProfiles("end2end")
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = TestContext.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EndToEndTesting {


    private static final Logger LOGGER = LoggerFactory.getLogger(EndToEndTesting.class);

    @Autowired
    private PipelineimagesProperties pipelineImagesProperties;

    public String dataConsumerImage = "data-consumer-service:0.0.1-SNAPSHOT";

    public String dataPersistenceImage = "";

    public String dataPublisherServiceImage = "";

    private Network end2endNetwork = Network.newNetwork();

    private CustomPostgreSQLContainer postgreSQLContainer;

    private CustomKafkaContainer kafkaContainer;


    @BeforeAll
    public void initContainers() {
        log.debug("Starting all the containers..." );
        postgreSQLContainer = CustomPostgreSQLContainer.getInstance();
        postgreSQLContainer.initialize(end2endNetwork);
        postgreSQLContainer.start(); // can be started together with Kafka by join TODO

        kafkaContainer = CustomKafkaContainer.getInstance();
        kafkaContainer.initialize(end2endNetwork);
        kafkaContainer.start(); // Can be started together

        initDataConsumerService();

    }

    private void initDataConsumerService() {
        log.debug("Initialize Data Consumer Service..");
        DataConsumerServiceContainer dataConsumerServiceContainer = DataConsumerServiceContainer.getInstance();
        dataConsumerServiceContainer
                .withNetwork(end2endNetwork)
                .dependsOn(postgreSQLContainer)
                .dependsOn(kafkaContainer)
                .withEnv(
                        Map.of("DATABASE_URL" ,  "jdbc:postgresql://host.docker.internal:" + postgreSQLContainer.getMappedPort(5432)+ "/"+ postgreSQLContainer.getDatabaseName(),
                                "DATABASE_USER", postgreSQLContainer.getUsername(),
                                "DATABASE_PASSWORD", postgreSQLContainer.getPassword(),
                                "BOOTSTRAP_SERVER", kafkaContainer.getBootstrapServers()))
                .withExposedPorts(8081)
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("ConsumerService"))
                .waitingFor(Wait.forLogMessage(".*Started DataConsumerService.*\\n", 1));

        dataConsumerServiceContainer.start();
        log.debug("Data Consumer Service started...");
    }


    @Test
    public void testDataPersist() {

        log.debug("nothing 111>>>>> {} ", postgreSQLContainer.getJdbcUrl());
        log.debug("kafka bootstrap address:: {}",kafkaContainer.getBootstrapServers());
    }

    @Test
    public void testDataPersist1() {
        log.debug("nothing 2222>>>>> {} ", postgreSQLContainer.getJdbcUrl());
    }


    @Test
    public void yamlLoadDemo() {
        LOGGER.info("Start of the demo test");
        if (ObjectUtils.isNotEmpty(pipelineImagesProperties) && ObjectUtils.isNotEmpty(pipelineImagesProperties.getImages())) {
            for (Image image : pipelineImagesProperties.getImages()
            ) {
                if ("popstgers-sql".equalsIgnoreCase(image.getName())) {

                    for (String applicationPropFileName : image.getApplicationProperty()) {
                         //override the application properties to the container which is instantiated
                        System.out.println("Load Postgresql Image name >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + image.getName());
                        System.out.println("Load Postgresql APP prop Names  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + image.getApplicationProperty().get(0));
                        System.out.println("Load Postgresql APP prop Names  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + image.getApplicationProperty().get(1));

                    }
                }
            }

        }
    }

}
