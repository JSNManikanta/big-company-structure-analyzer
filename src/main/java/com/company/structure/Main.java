package com.company.structure;

import com.company.structure.service.EmployeeServiceImpl;

import java.nio.file.Paths;

/**
 * Application entry point.
 */
public class Main {
    public static void main(String[] args) {
        new EmployeeServiceImpl().analyze(Paths.get("src/main/resources/employees.csv"));
    }
}
