package com.example.testcontainers;


import com.example.testcontainers.config.model.Image;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("pipelineimages")
public class PipelineimagesProperties {

    private List<Image> images = new ArrayList<>();
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "YAMLConfig{" +
                "image=" + images +
                '}';
    }

}
