package com.gic.ui;

import com.gic.service.StatementService;

import java.util.Scanner;

public class PrintStatementUI {

    private final Scanner scanner;
    private final StatementService statementService;

    public PrintStatementUI(Scanner scanner, StatementService statementService) {
        this.scanner = scanner;
        this.statementService = statementService;
    }

    public void start() {
        System.out.println("Please enter account and month to generate the statement <Account> <Year><Month>");
        System.out.println("(or enter blank to go back to main menu):");
        while (true) {
            System.out.print(">");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break;
            }
            processInput(line);
        }
    }

    public void processInput(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Please enter exactly 2 fields.");
            return;
        }
        statementService.printStatement(parts[0], parts[1]);
    }
}
