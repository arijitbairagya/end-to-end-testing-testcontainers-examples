package com.example.testcontainers;

import com.example.testcontainers.containers.CustomPostgreSQLContainer;
import com.example.testcontainers.containers.DataConsumerServiceContainer;
import com.example.testcontainers.properties.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Slf4j
@SpringBootTest(classes = EndToEndTesting.class)
@ActiveProfiles("end2end")
@EnableConfigurationProperties(value = TestContext.class)
//@TestPropertySource("classpath:application-end2end.yml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EndToEndTesting {

//    @Autowired
//    TestContext testContext;

    @Value("${postgresImage}")
    public String postgresImage;

    public String dataConsumerImage = "data-consumer-service:0.0.1-SNAPSHOT";

    public String dataPersistenceImage = "";

    public String dataPublisherServiceImage = "";

    private Network end2endNetwork = Network.newNetwork();

    private PostgreSQLContainer postgreSQLContainer;


    @BeforeAll
    public void initContainers() {
        log.debug("Starting all the containers..." );

        initPostgresContainer();
        log.debug("Starting postgreSQLContainer..." );
        postgreSQLContainer.start();


        initDataConsumerService();

    }

    private void initDataConsumerService() {
        log.debug("Initialize Data Consumer Service..");
        DataConsumerServiceContainer dataConsumerServiceContainer = DataConsumerServiceContainer.getInstance();
        dataConsumerServiceContainer
                .withNetwork(end2endNetwork)
                .dependsOn(postgreSQLContainer)
                .withEnv(
                        Map.of("DATABASE_URL" ,  "jdbc:postgresql://host.docker.internal:" + postgreSQLContainer.getMappedPort(5432)+ "/"+ postgreSQLContainer.getDatabaseName(),
                                "DATABASE_USER", postgreSQLContainer.getUsername(),
                                "DATABASE_PASSWORD", postgreSQLContainer.getPassword()))
                .withExposedPorts(8081)
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("ConsumerService"))
                .waitingFor(Wait.forLogMessage(".*Started DataConsumerService.*\\n", 1));

        dataConsumerServiceContainer.start();
        log.debug("Data Consumer Service started...");
    }

    private void initPostgresContainer() {
        log.debug("initPostgresContainer..." );
        postgreSQLContainer = CustomPostgreSQLContainer.getInstance();
        postgreSQLContainer.withUsername("super-admin")
                .withPassword("super-admin")
                .withExposedPorts(5432)
                .withReuse(true)
                .withNetwork(end2endNetwork)
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("PostgreSQL"))
                .waitingFor(Wait.forLogMessage(".*to accept connections.*\\n", 1));

    }

    @Test
    public void testDataPersist() {
        log.debug("nothing 111>>>>> {} ", postgreSQLContainer.getJdbcUrl());
    }

    @Test
    public void testDataPersist1() {
        log.debug("nothing 2222>>>>> {} ", postgreSQLContainer.getJdbcUrl());
    }


}
