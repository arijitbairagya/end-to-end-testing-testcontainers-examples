package com.example.testcontainers.consumer;

import com.example.testcontainers.consumer.config.kafka.KafkaProducer;
import com.example.testcontainers.consumer.dao.EmployeeRepository;
import com.example.testcontainers.consumer.dao.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = DataConsumerService.class)
class DataConsumerServiceTest {

	/*************** Application context loading - START *****************/
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	KafkaProducer kafkaProducer;



	/*************** Application context loading - END *****************/

	/************** Test Containers Setup - START ***************/
//	private static Network network = Network.newNetwork();

	/**
	 *
	 * static sql container is declared as this is required to have a single database instance for all the test cases.
	 * 	If static is not used then each test method will get a new database instance
	 * 	with re-use it will keep the container which is good for local development
	 *
	 */
	private static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(PostgreSQLContainer.IMAGE)
			.withUsername("admin")
			.withPassword("admin")
			.withDatabaseName("poc_docker_container")
//			.withNetwork(network)
			.withNetworkAliases("postgres")
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Postgres"))
			.withReuse(false); // reuse is used to keep the containers alive even after the test execution to

	private static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
//			.withNetwork(network)
			.withNetworkAliases("kafka")
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Kafka"))
			.withReuse(false);

	/************** Test Containers Setup - END ***************/

	@BeforeAll
	public static void setupContainers() {

		Startables.deepStart(postgreSQLContainer, kafkaContainer).join();

		System.setProperty("DATABASE_URL", postgreSQLContainer.getJdbcUrl());
		System.setProperty("DATABASE_USER", postgreSQLContainer.getUsername());
		System.setProperty("DATABASE_PASSWORD", postgreSQLContainer.getJdbcUrl());

		// add kafka properties
		System.setProperty("BOOTSTRAP_SERVER", kafkaContainer.getBootstrapServers());
	}

	/**
	 * Dynamic properties will be set to actual application context
	 *
	 * @param dynamicPropertyRegistry
	 */
	@DynamicPropertySource
	public static void overrideProperty(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.user", postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);

		dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
	}

	@Test
	void testInsertedDataToPostgres() {
		Employee savedEmp = employeeRepository.save(Employee.builder()
				.name("Bairagya")
				.build());
		log.debug(" DB Name: {}", postgreSQLContainer.getDatabaseName());
		log.debug(" Database URL: {}", postgreSQLContainer.getJdbcUrl());
		log.debug(" DB User Name: {}", postgreSQLContainer.getUsername());
		log.debug(" DB Password: {}", postgreSQLContainer.getPassword());

		// find the saved employee
		Optional<Employee> empRes = employeeRepository.findById(savedEmp.getId());

		assertThat(empRes).contains(Employee.builder().name(savedEmp.getName()).id(savedEmp.getId()).build());
	}

	@Test
	void testKafkaIntegration() {

		Employee newEmp = Employee.builder()
				.name("AbcDEf")
				.build();

		log.debug("Sending message to kafka..{} ", newEmp);

		kafkaProducer.sendMessage(newEmp.toString(), "consumer-topic");

		KafkaConsumer<String, String> kafkaConsumer = getConsumer(kafkaContainer);
		kafkaConsumer.subscribe(Collections.singletonList("data-consumer-out"));
//		Await
		Awaitility.await().untilAsserted(() ->  {
			ConsumerRecords<String, String> record = kafkaConsumer.poll(Duration.ofMillis(1000));
			record.forEach( rec -> {
				log.debug("Got record:: {}", rec.value());
				Assert.assertEquals("ABCDEF", rec.value());
			});
		});
	}

	private KafkaConsumer<String, String> getConsumer(
			KafkaContainer kafkaContainer) {

		return new KafkaConsumer<>(
				Map.of(
						ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
						kafkaContainer.getBootstrapServers(),
						ConsumerConfig.GROUP_ID_CONFIG,
						"tc-" + "cosumer342849",
						ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
						"earliest"),
				new StringDeserializer(),
				new StringDeserializer());
	}

}
