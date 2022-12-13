package com.example.testcontainers.consumer.controller;


import com.example.testcontainers.consumer.dao.EmployeeRepository;
import com.example.testcontainers.consumer.dao.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/create")
    public Employee createEmp(@RequestBody Employee employee) {
        return  employeeRepository.save(employee);
    }

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        log.debug("Get all emp request");
        return employeeRepository.findAll();
    }

    @GetMapping("/hello")
    public String sayHello() {
        log.debug("Only Hello .. ha ...");
        return "Only Hello...!";
    }
}