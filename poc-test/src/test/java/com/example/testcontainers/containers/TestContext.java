package com.example.testcontainers.containers;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
@Configuration
@ConfigurationProperties(prefix = "test-context")
@Data
@ToString
public class TestContext {

    public SystemUnderTest systemUnderTest;

    @Data
    public class SystemUnderTest {
        List<PipelineImage> pipelineImages;
    }

    @Data
    public class PipelineImage {
        String imageName;
    }
}
