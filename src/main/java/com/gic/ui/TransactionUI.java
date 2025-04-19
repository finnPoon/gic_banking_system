package com.gic.ui;

import com.gic.model.Transaction;
import com.gic.service.TransactionService;

import java.util.List;
import java.util.Scanner;

public class TransactionUI {

    private final Scanner scanner;
    private final TransactionService transactionService;

    public TransactionUI(Scanner scanner, TransactionService transactionService) {
        this.scanner = scanner;
        this.transactionService = transactionService;
    }

    public void start() {
        System.out.println("Please enter transaction details in <Date> <Account> <Type> <Amount> format ");
        System.out.println("(or enter blank to go back to main menu):");
        while (true) {
            System.out.print(">");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break;
            }
            processInput(line);
            break;
        }
    }

    public void processInput(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 4) {
            System.out.println("Invalid input format. Please enter exactly 4 fields.");
            return;
        }
        try {
            Transaction txn = transactionService.addTransaction(parts[0], parts[1], parts[2], parts[3]);
            printAccountStatement(parts[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printAccountStatement(String accountId) {
        List<Transaction> txns = transactionService.getAllTransactions(accountId);
        if (txns.isEmpty()) {
            System.out.println("No transactions found for account " + accountId);
            return;
        }
        System.out.println("Account: " + accountId);
        System.out.println("| Date     | Txn Id      | Type | Amount |");
        for (Transaction txn : txns) {
            System.out.printf("| %s | %-11s | %c    | %6.2f |\n",
                    txn.getDate().toString().replaceAll("-", ""),
                    txn.getTxnId(),
                    txn.getType(),
                    txn.getAmount());
        }
    }
}
