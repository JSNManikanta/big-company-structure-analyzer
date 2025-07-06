package com.company.structure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents an Employee entity.
 */
@Getter
@AllArgsConstructor
@ToString
public class Employee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final Integer managerId;

    public String getFullName() { return firstName + " " + lastName; }
}
