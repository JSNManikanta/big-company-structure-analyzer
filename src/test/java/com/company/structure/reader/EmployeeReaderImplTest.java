package com.company.structure.reader;

import com.company.structure.exception.CSVProcessingException;
import com.company.structure.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmployeeReaderImpl}.
 * Tests CSV reading, parsing, validation, and error handling.
 */
class EmployeeReaderImplTest {

    private EmployeeReaderImpl employeeReader;

    @BeforeEach
    void setUp() {
        employeeReader = new EmployeeReaderImpl();
    }

    /**
     * Creates a temporary CSV file with given content for testing.
     */
    private Path createTempCsvFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("employees", ".csv");
        Files.write(tempFile, content.getBytes());
        return tempFile;
    }

    /**
     * Tests that a valid CSV file is parsed correctly.
     */
    @Test
    void testReadValidCsv() throws Exception {
        Path tempFile = createTempCsvFile(
                "id,firstName,lastName,salary,managerId\n" +
                        "1,John,Doe,50000,2\n" +
                        "2,Jane,Smith,60000,\n" +
                        "3,Bob,Johnson,55000,1\n"
        );

        List<Employee> employees = employeeReader.read(tempFile);

        assertEquals(3, employees.size());

        Employee first = employees.get(0);
        assertEquals(1, first.getId());
        assertEquals("John", first.getFirstName());
        assertEquals("Doe", first.getLastName());
        assertEquals(50000, first.getSalary());
        assertEquals(2, first.getManagerId());

        Employee second = employees.get(1);
        assertNull(second.getManagerId());

        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests that the CSV reader throws an exception for an empty file.
     */
    @Test
    void testReadEmptyFileThrowsException() throws IOException {
        Path tempFile = createTempCsvFile("");

        CSVProcessingException exception = assertThrows(CSVProcessingException.class, () -> {
            employeeReader.read(tempFile);
        });
        assertTrue(exception.getMessage().contains("CSV file is empty"));

        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests that the CSV reader throws an exception for missing required headers.
     */
    @Test
    void testReadCsvWithMissingHeadersThrowsException() throws IOException {
        Path tempFile = createTempCsvFile(
                "id,firstName,lastName\n" +
                        "1,John,Doe\n"
        );

        CSVProcessingException exception = assertThrows(CSVProcessingException.class, () -> {
            employeeReader.read(tempFile);
        });
        assertTrue(exception.getMessage().contains("Missing columns"));

        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests that invalid file extension causes an exception.
     */
    @Test
    void testReadInvalidFileExtensionThrowsException() {
        Path fakeFile = Path.of("data.txt");

        CSVProcessingException exception = assertThrows(CSVProcessingException.class, () -> {
            employeeReader.read(fakeFile);
        });

        assertTrue(exception.getMessage().contains("Invalid file type"));
    }

    /**
     * Tests that the CSV reader can handle managerId field missing or empty, setting it to null.
     */
    @Test
    void testReadCsvWithMissingManagerIdIsNull() throws Exception {
        Path tempFile = createTempCsvFile(
                "id,firstName,lastName,salary,managerId\n" +
                        "11,Alice,Bob,75000,\n" +          // empty managerId
                        "12,Charlie,Dane,65000, \n"        // whitespace managerId
        );

        List<Employee> employees = employeeReader.read(tempFile);

        assertEquals(2, employees.size());
        assertNull(employees.get(0).getManagerId());
        assertNull(employees.get(1).getManagerId());

        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests that lines with extra columns beyond required headers are handled.
     */
    @Test
    void testReadCsvWithExtraColumnsIgnoresExtra() throws Exception {
        Path tempFile = createTempCsvFile(
                "id,firstName,lastName,salary,managerId,department,email\n" +
                        "13,Steve,Jobs,100000,5,Tech,steve@apple.com\n" +
                        "14,Bill,Gates,90000,6,Tech,bill@microsoft.com\n"
        );

        List<Employee> employees = employeeReader.read(tempFile);

        assertEquals(2, employees.size());
        assertEquals(13, employees.get(0).getId());
        assertEquals("Steve", employees.get(0).getFirstName());

        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests that blank lines between data lines are ignored/skipped without issues.
     */
    @Test
    void testReadCsvWithBlankLinesIgnored() throws Exception {
        Path tempFile = createTempCsvFile(
                "id,firstName,lastName,salary,managerId\n" +
                        "\n" +
                        "15,Emma,Stone,55000,7\n" +
                        "\n" +
                        "16,Brad,Pitt,57000,7\n" +
                        "\n"
        );

        List<Employee> employees = employeeReader.read(tempFile);

        assertEquals(2, employees.size());
        assertEquals(15, employees.get(0).getId());
        assertEquals(16, employees.get(1).getId());

        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests that a line with fewer columns than headers is skipped.
     */
    @Test
    void testReadCsvWithShortLineSkipped() throws Exception {
        Path tempFile = createTempCsvFile(
                "id,firstName,lastName,salary,managerId\n" +
                        "17,Michael,Jordan,70000,8\n" +
                        "18,Larry,Bird,60000\n" +     // Missing managerId column - skip
                        "19,Kobe,Bryant,80000,8\n"
        );

        List<Employee> employees = employeeReader.read(tempFile);

        assertEquals(2, employees.size());
        assertEquals(17, employees.get(0).getId());
        assertEquals(19, employees.get(1).getId());

        Files.deleteIfExists(tempFile);
    }
}
