package com.company.structure.service;

import com.company.structure.exception.CSVProcessingException;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmployeeServiceImpl}, which performs analysis on employee data
 * including salary validation and reporting line evaluation.
 *
 * Tests are based on various CSV file scenarios located in the test resources directory.
 * These include valid files, empty files, incorrect formats, and invalid extensions.
 */
class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;

    /**
     * Initializes a fresh instance of {@link EmployeeServiceImpl} before each test.
     */
    @BeforeEach
    void setup() {
        employeeService = new EmployeeServiceImpl();
    }

    /**
     * Loads a CSV test resource from the classpath into a {@link Path}.
     *
     * @param fileName name of the CSV file within the resources folder
     * @return path to the file for testing purposes
     */
    private Path getResourcePath(String fileName) {
        try {
            return Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            fail("Test resource file not found: " + fileName);
            return null; // Never reached due to fail()
        }
    }

    /**
     * Validates that a properly structured CSV file is analyzed without any exceptions.
     */
    @Test
    @DisplayName("Valid employees.csv file should be analyzed without exceptions")
    void analyzeValidEmployeesCsv() {
        Path csvPath = getResourcePath("employees.csv");
        assertDoesNotThrow(() -> employeeService.analyze(csvPath));
    }

    /**
     * Validates that an empty CSV file results in an appropriate exception.
     */
    @Test
    @DisplayName("Empty CSV file should throw CSVProcessingException")
    void analyzeEmptyFileCsv() {
        Path csvPath = getResourcePath("empty_file.csv");
        CSVProcessingException ex = assertThrows(CSVProcessingException.class,
                () -> employeeService.analyze(csvPath));
        assertTrue(ex.getMessage().contains("CSV file is empty"), "Expected error about empty file");
    }

    /**
     * Ensures that files with unsupported extensions (e.g., .txt) are rejected.
     */
    @Test
    @DisplayName("File with invalid extension should throw CSVProcessingException")
    void analyzeInvalidExtensionFile() {
        Path csvPath = getResourcePath("invalid_file.txt");
        CSVProcessingException ex = assertThrows(CSVProcessingException.class,
                () -> employeeService.analyze(csvPath));
        assertTrue(ex.getMessage().contains("Invalid file type"), "Expected error about invalid file type");
    }

    /**
     * Tests whether the application correctly flags CSVs that are missing mandatory columns.
     */
    @Test
    @DisplayName("CSV with missing columns should throw CSVProcessingException")
    void analyzeMissingColumnsCsv() {
        Path csvPath = getResourcePath("missing_column.csv");
        CSVProcessingException ex = assertThrows(CSVProcessingException.class,
                () -> employeeService.analyze(csvPath));
        assertTrue(ex.getMessage().toLowerCase().contains("missing"),
                "Expected error about missing columns");
    }
}
