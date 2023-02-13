# Microservices Testing Strategies
In microservice architectural style, services are connected with each other over networks and make use of external data stores. These network
partitions affects the style of testing. In a large system multiple services work together to meet business functionalities and multiple teams are
responsible to maintain those services. In some cases asynchronous publish-subscribe communication mechanism is more useful
than synchronous point-to-point communication. Automated test should provide the coverage for each of these services to identify the failure points and help to 
build resilient system. 

### Different Approaches for microservice testing 
* ### **Unit Testing**  
    In this testing approach we normally test smallest unit of code which are written at class level or considering group of relates classes. 
* ### **Integration Testing**
  In microservice architecture these are used to verify interactions between layers of integration code and external components with which the services are interacting. 
* ### **End-To-End Testing(E2E)**
  An end-to-end test verifies that a system meets external requirements and achieve its goal by testing the entire system end to end. To achieve this the system under test is considered as black box and manipulate this through public interfaces. 

Here we are going to concentrate mainly E2E testing approach and how this can be integrated with CI pipeline to perform the automation testing. 

One of the most popular approach today to perform microservices E2E test is to set up a separate test environment and run containerized applications to perform the test. This approach is having multiple problems 

- We still need to use the network services, for example data stores, kafka etc which are required to be in a clean state or desired state before the execution of E2E test
- Any data manipulation by one team can affect the test execution of another team which leads to stringent test data management
- As different teams want to run their own services which is under development, it will become a costly affair to run multiple test instances with the required services

Let's consider the following example  

![data-flow-diagram.jpg](..%2Fdata-flow-diagram.jpg)


# End-To-End Testing Using TestContainers Framework

This project is an example of how to test microservices separately using docker and test containers(https://www.testcontainers.org/)

Following picture illustrate the functionality for the microservices - 
![End2End-Microservice-Testing-Architecture v1 0](https://user-images.githubusercontent.com/17141306/208586936-9647aaf3-a196-4471-a4fc-db73270bb61b.jpg)


# Build Docker Images
Go to project folder and build images using maven command - **mvn spring-boot:build-image**

# Run End-To-End Test


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
4. consume message:: kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic consumer-topic --from-beginning

