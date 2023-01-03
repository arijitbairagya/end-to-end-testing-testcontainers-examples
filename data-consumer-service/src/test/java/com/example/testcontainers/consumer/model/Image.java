package com.example.testcontainers.consumer.model;

import java.util.List;

public class Image {

    private String name;

    private List<String> applicationProperty;
    private String environmentProperty;

    public String getName() {
        return name;
    }

    public void setName(String imageName) {
        this.name = imageName;
    }

    public List<String> getApplicationProperty() {
        return applicationProperty;
    }

    public void setApplicationProperty(List<String> applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    public String getEnvironmentProperty() {
        return environmentProperty;
    }

    public void setEnvironmentProperty(String environmentProperty) {
        this.environmentProperty = environmentProperty;
    }
    @Override
    public String toString() {
        return "Image{" +
                "name='" + name + '\'' +
                ", applicationProperty='" + applicationProperty + '\'' +
                ", environmentProperty='" + environmentProperty + '\'' +
                '}';
    }


}
