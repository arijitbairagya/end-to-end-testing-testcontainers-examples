package com.example.testcontainers.containers;

import com.example.testcontainers.DockerImages;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class DataConsumerServiceContainer extends GenericContainer {

    private static DataConsumerServiceContainer dataConsumerServiceContainer;

    private DataConsumerServiceContainer() {
        super(DockerImageName.parse(DockerImages.DATA_CONSUMER_IMAGE));
    }

    public static DataConsumerServiceContainer getInstance() {
        if(dataConsumerServiceContainer == null)
            dataConsumerServiceContainer = new DataConsumerServiceContainer();
        return dataConsumerServiceContainer;
    }
}
