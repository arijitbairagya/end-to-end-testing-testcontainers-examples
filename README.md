

# TestContainers End To End Microservice Testing 

This project is an example of how to test microservices separately using docker and test containers(https://www.testcontainers.org/)

Following picture illustrate the functionality for the microservices - 
![End2End-Microservice-Testing-Architecture v1 0](https://user-images.githubusercontent.com/17141306/208586936-9647aaf3-a196-4471-a4fc-db73270bb61b.jpg)


# Build Docker Images
Go to project folder and build images using maven command - **mvn spring-boot:build-image**

# Run End To End Test


# Additional notes

- Kafka Installation for Mac

1. Download the latest version of Apache Kafka from https://kafka.apache.org/downloads under Binary downloads.

2. Click on any of the binary downloads (it is preferred to choose the most recent Scala version - example 2.13). For this illustration, we will assume version 2.13-3.0.0.

3. Download and extract the contents (double click in the Finder) to a directory of your choice, for example ~/kafka_2.13-3.0.0 .

4. Navigate to the root of the Apache Kafka folder and open a Terminal. Or Open a Terminal and navigate to the root directory of Apache Kafka. For this example, we will assume that the Kafka download is expanded into the ~/kafka_2.13-3.0.0 directory.

- Run Kafka
1. Start Zookeeper $ ~/kafka_2.13-3.3.1/bin/zookeeper-server-start.sh ~/kafka_2.13-3.3.1/config/zookeeper.properties
2. Start Kafka server $ ~/kafka_2.13-3.3.1/bin/kafka-server-start.sh ~/kafka_2.13-3.3.1/config/server.properties
- Or Setup the $PATH environment variable
PATH="$PATH:/Users/arijit/kafka_2.13-3.3.1/bin"
3. publish message:: kafka-console-producer.sh --broker-list localhost:9092 --topic consumer-topic
4. consumer message:: kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic consumer-topic --from-beginning

