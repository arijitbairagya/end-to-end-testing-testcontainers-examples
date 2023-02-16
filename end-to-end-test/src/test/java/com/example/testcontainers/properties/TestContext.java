//package com.example.testcontainers.properties;
//
//import lombok.Data;
//import lombok.ToString;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//
//@Data
//@ToString
//@ConfigurationProperties(prefix = "test-context")
//public class TestContext {
//
//    private SystemUnderTest systemUnderTest;
//
//    @Data
//    public class SystemUnderTest {
//        List<PipelineImage> pipelineImages;
//    }
//
//    @Data
//    public class PipelineImage {
//        String imageName;
//        String applicationProperty;
//    }
//}
