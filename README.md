# Big Company Structure Analyzer

## Overview
This Java application analyzes the organizational structure of a company based on employee data in a CSV file. It validates:

- Managers earning appropriately compared to their subordinates (more than 20% to 50% higher salary).
- Employees with reporting lines longer than 4 levels between them and the CEO.

## Features
Reads employees data from CSV file and prints employees list after validation to console.
- which managers earn less than they should, and by how much
- which managers earn more than they should, and by how much
- which employees have a reporting line which is too long, and by how much

## Project Structure
- `model` - Contains the Employee class.
- `reader` - Reads employee data from CSV.
- `validator` - Validates salary and reporting line constraints.
- `service` - Coordinates reading and validation.
- `Main` - Entry point for the application.

## Requirements
- Java 8+
- Maven
- Lombok (IDE plugin recommended)
- JUnit 5 for tests 
- Place your employee CSV file (e.g.`employees.csv`) in `src/main/resources/`. 
- Build and Run the application
