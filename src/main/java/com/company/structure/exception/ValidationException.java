package com.company.structure.exception;

/**
 * Throw exception on failure of business validation.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String msg) { super(msg); }
}
