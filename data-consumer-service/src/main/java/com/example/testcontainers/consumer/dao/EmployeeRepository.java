package com.example.testcontainers.consumer.dao;

import com.example.testcontainers.consumer.dao.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
