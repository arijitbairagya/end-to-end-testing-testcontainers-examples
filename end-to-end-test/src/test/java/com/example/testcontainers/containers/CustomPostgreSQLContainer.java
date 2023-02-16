package com.example.testcontainers.containers;


import com.example.testcontainers.DockerImages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

@Slf4j
public class CustomPostgreSQLContainer extends PostgreSQLContainer {

    private static CustomPostgreSQLContainer postgreSQLContainer;

    private CustomPostgreSQLContainer() {
        super(DockerImageName.parse(DockerImages.POSTGRESQL_IMAGE).asCompatibleSubstituteFor("postgres"));
    }

    // one instance per test class
    public static CustomPostgreSQLContainer getInstance() {
        if(postgreSQLContainer == null) {
            postgreSQLContainer = new CustomPostgreSQLContainer();
        }
        return postgreSQLContainer;
    }

    public void initialize(Network network) {
        log.debug("initPostgresContainer..." );
        postgreSQLContainer.withUsername("super-admin")
                .withPassword("super-admin")
                .withExposedPorts(5432)
                .withReuse(true)
                .withNetwork(network)
                .withNetworkAliases("postgres")
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("PostgreSQL"))
                .waitingFor(Wait.forLogMessage(".*to accept connections.*\\n", 1));
    }


    @Override
    public void start() {
        super.start();
        System.setProperty("DATABASE_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DATABASE_USER", postgreSQLContainer.getUsername());
        System.setProperty("DATABASE_PASSWORD", postgreSQLContainer.getPassword());
    }
}
