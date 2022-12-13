package com.example.testcontainers.containers;


import com.example.testcontainers.DockerImages;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

public class CustomPostgreSQLContainer extends PostgreSQLContainer {

    private static CustomPostgreSQLContainer postgreSQLContainer;

    private CustomPostgreSQLContainer() {
        super(DockerImageName.parse(DockerImages.POSTGRESQL_IMAGE).asCompatibleSubstituteFor("postgres"));
    }

    // one instance per test class
    public static CustomPostgreSQLContainer getInstance() {
        if(postgreSQLContainer == null)
            postgreSQLContainer = new CustomPostgreSQLContainer();



        return postgreSQLContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DATABASE_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DATABASE_USER", postgreSQLContainer.getUsername());
        System.setProperty("DATABASE_PASSWORD", postgreSQLContainer.getPassword());
    }
}
