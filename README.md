# Simple GIC Banking Application

## How to Run the Application

### Prerequisites

-   Java Development Kit (JDK) version 17 or higher installed on your system. Version 17 is preferred.

### Steps

1.  **Clone the Repository:** https://github.com/finnPoon/gic_banking_system.git
2.  **Using an IDE (Preferably IntelliJ IDEA)**:
    -   Open the project in your IDE.
    -   Compile and build the application
    -   Navigate to the `com.gic.Main` class.
    -   Run the `main` method.

Alternatively, you can follow these steps to run the application without and IDE:
1.  **Clone the Repository:** https://github.com/finnPoon/gic_banking_system.git
2.  **Compile the Application:**
    - Navigate to the root directory of the project in your terminal.
    - Compile all Java files using the following command:
      - javac com/gic/.java com/gic/model/.java com/gic/service/.java com/gic/ui/.java com/gic/util/*.java
3.  **Run the Application:**
    - After successful compilation, run the application with:
      - java com/gic/Main

## Design Concepts

The application is designed with the following key concepts in mind:

### 1. Modular Architecture

The application is structured into several layers and classes:

-   **UI Layer:** Handles user input and output.
    -   `UIHelper`: Manages the main menu and overall application flow.
    -   `TransactionUI`, `InterestRuleUI`, `PrintStatementUI`: Specific UI components for transaction input, interest rule definition, and statement printing.
-   **Service Layer:** Contains the business logic of the application.
    -   `TransactionService`: Manages accounts and processes transactions.
    -   `InterestRuleService`: Manages interest rules.
    -   `StatementService`: Generates account statements, including interest calculation.
-   **Model Layer:** Defines the data structures used by the application.
    -   `Account`: Represents a bank account and its transactions.
    -   `Transaction`: Represents a financial transaction.
    -   `InterestRule`: Represents an interest rule.
-   **Utility Layer:** Provides utility functions, including:
    -   `DateUtil`: Handles date parsing and formatting.
    -   `InputUtil`: Handles input validation.

### 2. Data Management

The application uses in-memory data storage, making it simple to run without external databases. The use of `HashMap`, `TreeMap` makes it easy to manage these codes.


### 3. Code Style

The coding follows these principles:
*   Single Responsibility: Each class has a specific purpose, such as printing transactions.
*   Utility Classes: Utility classes have been introduced for various functions, which reduces the amount of duplicated codes.



