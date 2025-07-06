package com.company.structure.validator;

import com.company.structure.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmployeeValidatorImpl}, which validates:
 * - Manager salaries within defined thresholds compared to their subordinates.
 * - Employees with reporting chains deeper than allowed.
 */
class EmployeeValidatorImplTest {

    private EmployeeValidatorImpl validator;

    /**
     * Initializes the validator before each test.
     */
    @BeforeEach
    void setup() {
        validator = new EmployeeValidatorImpl(); // Concrete validator implementation
    }

    /**
     * Utility method to create a list of employees from object arrays.
     *
     * @param employeeData variable number of object arrays representing employee records
     * @return list of {@link Employee} objects
     */
    private static List<Employee> createEmployeeList(Object... employeeData) {
        List<Employee> employees = new ArrayList<>();
        for (Object data : employeeData) {
            Object[] emp = (Object[]) data;

            int id = (int) emp[0];
            String firstName = (String) emp[1];
            String lastName = (String) emp[2];
            double salary = ((Number) emp[3]).doubleValue();
            Integer managerId = (Integer) emp[4];

            employees.add(new Employee(id, firstName, lastName, salary, managerId));
        }
        return employees;
    }

    /**
     * Tests that a realistic hierarchy produces no salary validation errors
     * or reporting length violations.
     */
    @Test
    void testValidRealisticHierarchy() {
        List<Employee> employees = createEmployeeList(
                new Object[]{123, "Joe", "Doe", 70000.0, null},
                new Object[]{124, "Martin", "Chekov", 62000.0, 123},
                new Object[]{125, "Bob", "Ronstad", 47000.0, 123},
                new Object[]{300, "Alice", "Hasacat", 50000.0, 124},
                new Object[]{305, "Brett", "Hardleaf", 34000.0, 300}
        );

        Map<String, List<String>> salaryRangeFindings = validator.validateManagerSalary(employees);
        List<String> reportingLengthFindings = validator.validateReportingLength(employees);

        assertTrue(salaryRangeFindings.get("less").isEmpty(), "Expected no 'below salary range' findings");
        assertTrue(salaryRangeFindings.get("more").isEmpty(), "Expected no 'above salary range' findings");
        assertTrue(reportingLengthFindings.isEmpty(), "Expected no extended reporting length findings");
    }

    /**
     * Parameterized test to validate both underpaid and overpaid managers.
     *
     * @param employees           list of employee data
     * @param expectedKey         either "less" or "more"
     * @param expectedNamePart    expected name to appear in the error message
     */
    @ParameterizedTest
    @MethodSource("provideManagerSalaryRangeTestData")
    void testValidateManagerSalaryRange(List<Employee> employees, String expectedKey, String expectedNamePart) {
        Map<String, List<String>> result = validator.validateManagerSalary(employees);

        List<String> messages = result.get(expectedKey);
        assertFalse(messages.isEmpty(), "Expected salary range finding in group: " + expectedKey);
        assertTrue(messages.get(0).contains(expectedNamePart), "Expected message to contain: " + expectedNamePart);
    }

    /**
     * Provides test cases for managers whose salaries are out of acceptable range.
     *
     * @return stream of arguments for parameterized test
     */
    static Stream<Arguments> provideManagerSalaryRangeTestData() {
        return Stream.of(
                // Manager earns less than subordinates' average
                Arguments.of(
                        createEmployeeList(
                                new Object[]{101, "Sophia", "Martinez", 30000.0, null},
                                new Object[]{201, "Lucas", "Johnson", 45000.0, 101},
                                new Object[]{202, "Emma", "Garcia", 47000.0, 101}
                        ),
                        "less",
                        "Sophia Martinez"
                ),
                // Manager earns more than subordinates' average
                Arguments.of(
                        createEmployeeList(
                                new Object[]{102, "Benjamin", "Walker", 120000.0, null},
                                new Object[]{203, "Lily", "Hernandez", 35000.0, 102},
                                new Object[]{204, "James", "Lopez", 37000.0, 102}
                        ),
                        "more",
                        "Benjamin Walker"
                )
        );
    }

    /**
     * Verifies that an employee exceeding the max reporting length (5th level) is flagged.
     */
    @Test
    void testReportingLengthExceedsLimit() {
        List<Employee> employees = createEmployeeList(
                new Object[]{1, "Joe", "Doe", 100000, null},
                new Object[]{2, "Martin", "Chekov", 80000, 1},
                new Object[]{3, "Alice", "Hasacat", 70000, 2},
                new Object[]{4, "Bob", "Ronstad", 60000, 3},
                new Object[]{5, "Sarah", "Knight", 50000, 4},
                new Object[]{6, "Brett", "Hardleaf", 40000, 5}
        );

        List<String> reportingLengthFindings = validator.validateReportingLength(employees);
        assertEquals(1, reportingLengthFindings.size(), "Expected one extended reporting length finding");
        assertTrue(reportingLengthFindings.get(0).contains("Brett Hardleaf"), "Expected finding for Brett Hardleaf");
    }

    /**
     * Ensures the validator handles empty input gracefully without errors.
     */
    @Test
    void testEmptyEmployeeList() {
        Map<String, List<String>> salaryRangeFindings = validator.validateManagerSalary(Collections.emptyList());
        List<String> reportingLengthFindings = validator.validateReportingLength(Collections.emptyList());

        assertTrue(salaryRangeFindings.get("less").isEmpty(), "Expected no 'below salary range' findings for empty list");
        assertTrue(salaryRangeFindings.get("more").isEmpty(), "Expected no 'above salary range' findings for empty list");
        assertTrue(reportingLengthFindings.isEmpty(), "Expected no extended reporting length findings for empty list");
    }
}
