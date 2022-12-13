package com.example.testcontainers.containers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest(classes = PocTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableConfigurationProperties(value = TestContext.class)
@TestPropertySource("classpath:application.yml")
public class PocTest {

    @Autowired
    TestContext testContext;

    @BeforeAll
    public void init() {
        log.debug("Context:: {}", testContext);
    }

    @Test
    public void test1() {
        log.debug("Nothing...{}", testContext.getSystemUnderTest().getPipelineImages());
    }


}
