package com.company.structure.reader;

import com.company.structure.exception.CSVProcessingException;
import com.company.structure.model.Employee;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EmployeeReader} that reads employee data from a CSV file.
 * Supports flexible column ordering and validates required fields.
 */
@Slf4j
public class EmployeeReaderImpl implements EmployeeReader {

    private static final List<String> REQUIRED_HEADERS = Arrays.asList(
            "id", "firstName", "lastName", "salary", "managerId"
    );

    /**
     * Reads and parses employee data from a CSV file into a list of Employee objects.
     *
     * @param csvPath Path to the CSV file
     * @return List of parsed Employee objects
     * @throws CSVProcessingException if the file is empty, malformed, or unreadable
     */
    @Override
    public List<Employee> read(Path csvPath) throws CSVProcessingException {
        validateFileExtension(csvPath);

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String headerLine = Optional.ofNullable(reader.readLine())
                    .orElseThrow(() -> new CSVProcessingException("CSV file is empty."));

            Map<String, Integer> headerIndexMap = parseHeaders(headerLine);

            return reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> parseLineToEmployee(line, headerIndexMap))
                    .filter(Objects::nonNull)  // skip malformed or short lines
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new CSVProcessingException("Error reading file: " + csvPath.getFileName(), e);
        }
    }

    /**
     * Validates that the provided file has a .csv extension.
     *
     * @param csvPath file path to check
     * @throws CSVProcessingException if file does not end with ".csv"
     */
    private void validateFileExtension(Path csvPath) {
        if (!csvPath.toString().toLowerCase().endsWith(".csv")) {
            throw new CSVProcessingException("Invalid file type. Only .csv files are supported.");
        }
    }

    /**
     * Parses the CSV header line and validates required columns.
     *
     * @param headerLine first line from the CSV file
     * @return Map of column names to their index positions
     * @throws CSVProcessingException if any required columns are missing
     */
    private Map<String, Integer> parseHeaders(String headerLine) {
        String[] headers = headerLine.split(",", -1);
        Map<String, Integer> headerIndexMap = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(headers[i].trim(), i);
        }

        List<String> missing = REQUIRED_HEADERS.stream()
                .filter(required -> !headerIndexMap.containsKey(required))
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            throw new CSVProcessingException("Missing columns: " + String.join(", ", missing));
        }

        return headerIndexMap;
    }

    /**
     * Parses a single CSV line into an {@link Employee} object.
     * Returns null if the line has insufficient columns.
     *
     * @param line           the CSV record line
     * @param headerIndexMap map of header names to their index positions
     * @return parsed Employee object or null if the line is invalid or incomplete
     * @throws CSVProcessingException if the line contains invalid values
     */
    private Employee parseLineToEmployee(String line, Map<String, Integer> headerIndexMap) {
        try {
            String[] fields = line.split(",", -1);

            // Check if line has all required columns
            if (fields.length <= Collections.max(headerIndexMap.values())) {
                log.warn("Skipping line with insufficient columns: {}", line);
                return null;
            }

            int id = Integer.parseInt(fields[headerIndexMap.get("id")].trim());
            String firstName = fields[headerIndexMap.get("firstName")].trim();
            String lastName = fields[headerIndexMap.get("lastName")].trim();
            double salary = Double.parseDouble(fields[headerIndexMap.get("salary")].trim());

            String managerIdStr = fields[headerIndexMap.get("managerId")].trim();
            Integer managerId = managerIdStr.isEmpty() ? null : Integer.parseInt(managerIdStr);

            return new Employee(id, firstName, lastName, salary, managerId);

        } catch (NumberFormatException e) {
            log.error("Invalid number format in line: {}", line, e);
            throw new CSVProcessingException("Bad record: " + line, e);
        } catch (Exception e) {
            log.error("Failed to parse line: {}", line, e);
            throw new CSVProcessingException("Bad record: " + line, e);
        }
    }
}
