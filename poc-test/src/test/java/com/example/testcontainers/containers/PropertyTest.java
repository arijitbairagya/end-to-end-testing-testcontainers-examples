package com.example.testcontainers.containers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@SpringBootTest(classes = PropertyTest.class)
@EnableConfigurationProperties(value = TestContext.class)
//@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
//@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PropertyTest {

    @Value("${postgresImage}")
    String postgresImage;

    @Autowired
    TestContext testContext;

    @Test
    public void propertyTest() {
         log.debug("Test Context:: {}", testContext);
         log.debug("postgresImage ::{}", postgresImage);
    }
}
