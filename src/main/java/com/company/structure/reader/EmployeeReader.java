package com.company.structure.reader;

import com.company.structure.exception.CSVProcessingException;
import com.company.structure.model.Employee;

import java.nio.file.Path;
import java.util.List;

/**
 * For reading employee data from a CSV file.
 */
public interface EmployeeReader {

    /**
     * Reads and parses employee data from the provided CSV file.
     *
     * @param csvPath the path to the CSV file.
     * @return list of Employee objects parsed from the file.
     * @throws CSVProcessingException if there is a problem with reading or parsing the file.
     */
    List<Employee> read(Path csvPath) throws CSVProcessingException;
}
