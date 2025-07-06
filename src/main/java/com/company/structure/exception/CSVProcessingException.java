package com.company.structure.exception;

/**
 * Throw exception on failures CSV file read and parse.
 */
public class CSVProcessingException extends RuntimeException {
    public CSVProcessingException(String msg) { super(msg); }
    public CSVProcessingException(String msg, Throwable cause) { super(msg, cause); }
}
