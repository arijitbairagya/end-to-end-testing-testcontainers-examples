package com.example.testcontainers.consumer;

import com.example.testcontainers.consumer.DataConsumerService;
import com.example.testcontainers.consumer.dao.EmployeeRepository;
import com.example.testcontainers.consumer.dao.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = DataConsumerService.class)
class DataConsumerServiceTest {

	@Autowired
	EmployeeRepository employeeRepository;

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
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Postgres"))
			.withReuse(true); // reuse is used to keep the containers alive even after the test execution to

	@BeforeAll
	public static void setupContainers() {
		postgreSQLContainer.start();

		System.setProperty("DATABASE_URL", postgreSQLContainer.getJdbcUrl());
		System.setProperty("DATABASE_USER", postgreSQLContainer.getUsername());
		System.setProperty("DATABASE_PASSWORD", postgreSQLContainer.getJdbcUrl());
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
	}

	@Test
	void testInsertedData() {
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

}
