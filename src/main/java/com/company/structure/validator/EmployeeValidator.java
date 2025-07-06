package com.company.structure.validator;

import com.company.structure.model.Employee;
import java.util.List;
import java.util.Map;

/**
 * Validates Salary and Reporting Length.
 */
public interface EmployeeValidator {
    Map<String, List<String>> validateManagerSalary(List<Employee> employees);
    List<String> validateReportingLength(List<Employee> employees);
}
