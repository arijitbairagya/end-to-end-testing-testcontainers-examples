package com.example.testcontainers.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class AbstractIntegrationTest {

    /** This is the docker network **/
    public static Network dockerNetwork = Network.newNetwork();


    /**
     *
     * static sql container is declared as this is required to have a single database instance for all the test cases.
     * 	If static is not used then each test method will get a new database instance
     * 	with re-use it will keep the container which is good for local development
     *
     */
    public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer)
            new PostgreSQLContainer(PostgreSQLContainer.IMAGE)
            .withUsername("admin")
            .withPassword("admin")
            .withDatabaseName("poc_docker_container")
            .withNetwork(dockerNetwork)
            .withNetworkAliases("postgres")
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Postgres-Test-Container"))
            .withReuse(false); // reuse is used to keep the containers alive even after the test execution to


    /**
     *
     * static kafka container is declared as this is required to have a single kafka setup for all the test cases.
     * 	If static is not used then each test method will get a new kafka instance
     * 	with re-use it will keep the container which is good for local development
     *
     */
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withNetwork(dockerNetwork)
            .withNetworkAliases("kafka")
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Kafka-Test-Container"))
            .withReuse(false);


    public static void initContainers() {

        // start all the independent containers together
        Startables.deepStart(postgreSQLContainer, kafkaContainer).join();

        System.setProperty("DATABASE_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DATABASE_USER", postgreSQLContainer.getUsername());
        System.setProperty("DATABASE_PASSWORD", postgreSQLContainer.getJdbcUrl());

        // add kafka properties
        log.debug("set kafka container properties.. BootstrapServers:{}", kafkaContainer.getBootstrapServers());
        log.debug("kafka host::{} ",kafkaContainer.getHost());
        log.debug("kafka port::{} ", kafkaContainer.getFirstMappedPort());
        System.setProperty("BOOTSTRAP_SERVER", kafkaContainer.getBootstrapServers());
    }

//    /**
//     * Dynamic properties will be set to actual application context if we load the application context instead of loading
//     * docker image
//     *
//     * @param dynamicPropertyRegistry
//     */
//    @DynamicPropertySource
//    public static void overrideProperty(DynamicPropertyRegistry dynamicPropertyRegistry) {
//        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        dynamicPropertyRegistry.add("spring.datasource.user", postgreSQLContainer::getUsername);
//        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//
//        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
//    }

}
