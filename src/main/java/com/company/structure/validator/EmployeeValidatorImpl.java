package com.company.structure.validator;

import com.company.structure.model.Employee;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EmployeeValidator} that validates:
 * 1. If a manager's salary is within 20%–50% range of their subordinates' average salary.
 * 2. If any employee has a reporting length more than 4 levels.
 */
public class EmployeeValidatorImpl implements EmployeeValidator {

    private static final String LESS_KEY = "less";
    private static final String MORE_KEY = "more";
    private static final int MAX_REPORTING_LENGTH = 4;

    /**
     * Validates manager salary against the average salary of their direct subordinates.
     * <p>
     * Returns two lists:
     * - "less": managers earning >20% less than their subordinates' average salary.
     * - "more": managers earning >50% more than their subordinates' average salary.
     *
     * @param employees list of all employees
     * @return a map with keys "less" and "more" containing respective messages
     */
    @Override
    public Map<String, List<String>> validateManagerSalary(List<Employee> employees) {
        final Map<Integer, Employee> idToEmployee = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity(), (e1, e2) -> e1));

        final Map<String, List<String>> result = new HashMap<>();
        result.put(LESS_KEY, new ArrayList<>());
        result.put(MORE_KEY, new ArrayList<>());

        final Map<Integer, List<Employee>> reportsByManager = employees.stream()
                .filter(e -> e.getManagerId() != null)
                .collect(Collectors.groupingBy(Employee::getManagerId));

        reportsByManager.forEach((managerId, subordinates) -> {
            Employee manager = idToEmployee.get(managerId);
            if (manager == null || subordinates.isEmpty()) return;

            double avgSubSalary = subordinates.stream()
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0.0);

            final double lowerBound = avgSubSalary * 1.2;
            final double upperBound = avgSubSalary * 1.5;
            final double managerSalary = manager.getSalary();

            if (managerSalary < lowerBound) {
                double diff = lowerBound - managerSalary;
                result.get(LESS_KEY).add(String.format("%s earns %.1f less than their subordinates average salary",
                        manager.getFullName(), diff));
            } else if (managerSalary > upperBound) {
                double diff = managerSalary - upperBound;
                result.get(MORE_KEY).add(String.format("%s earns %.1f more than their subordinates average salary",
                        manager.getFullName(), diff));
            }
        });

        return result;
    }

    /**
     * Validates the reporting length (hierarchy levels under CEO) for each employee.
     * Identifies employees whose reporting length is more than {@code MAX_REPORTING_LENGTH} levels.
     *
     * @param employees the list of all employees
     * @return list of strings in the format: "<FullName> reporting line length is: <length>" if length > MAX_REPORTING_LENGTH
     */
    @Override
    public List<String> validateReportingLength(List<Employee> employees) {
        final Map<Integer, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity(), (e1, e2) -> e1));

        return employees.stream()
                .map(employee -> new AbstractMap.SimpleEntry<>(employee, calculateReportingLength(employee, employeeMap)))
                .filter(entry -> entry.getValue() > MAX_REPORTING_LENGTH)
                .map(entry -> String.format("%s reporting line length is: %d", entry.getKey().getFullName(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private int calculateReportingLength(Employee employee, Map<Integer, Employee> employeeMap) {
        int length = 0;
        Integer managerId = employee.getManagerId();
        while (managerId != null && employeeMap.containsKey(managerId)) {
            managerId = employeeMap.get(managerId).getManagerId();
            length++;
        }
        return length;
    }
}
