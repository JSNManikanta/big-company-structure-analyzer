package com.company.structure.service;

import java.nio.file.Path;

/**
 * Service for analyzing employee hierarchy, salary ranges, and reporting length.
 */
public interface EmployeeService {
    /**
     * Analyzes the employee CSV file for salary range evaluations and reporting length.
     *
     * @param csvPath the path to the employee CSV file
     */
    void analyze(Path csvPath);
}
