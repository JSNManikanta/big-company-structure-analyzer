package com.company.structure.service;

import com.company.structure.model.Employee;
import com.company.structure.reader.EmployeeReader;
import com.company.structure.reader.EmployeeReaderImpl;
import com.company.structure.validator.EmployeeValidator;
import com.company.structure.validator.EmployeeValidatorImpl;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * EmployeeServiceImpl handles reading employee data from a CSV file,
 * validating the data and printing salary differences and reporting length.
 */
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeReader reader = new EmployeeReaderImpl();
    private final EmployeeValidator validator = new EmployeeValidatorImpl();

    /**
     * This method reads employee data from the CSV file,
     * performs validations, and prints the output to the console.
     *
     * It checks for:
     * - Managers whose salaries are below 20% and above 50% when compared to their subordinates.
     * - The length of the reporting chain for each employee.
     *
     * @param csvPath the path to the CSV file containing employee data
     */
    @Override
    public void analyze(Path csvPath) {
        List<Employee> employees = reader.read(csvPath);

        System.out.println("=========== Manager Salary (below 20%) ===========");
        Map<String, List<String>> discrepancyEmployeesList = validator.validateManagerSalary(employees);
        discrepancyEmployeesList.get("less").forEach(System.out::println);

        System.out.println();

        System.out.println("=========== Manager Salary (beyond 20%) ===========");
        discrepancyEmployeesList.get("more").forEach(System.out::println);

        System.out.println();

        System.out.println("=========== Reporting Line Length ===========");
        validator.validateReportingLength(employees).forEach(System.out::println);
    }
}
