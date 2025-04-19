package com.gic.ui;

import com.gic.service.InterestRuleService;
import com.gic.service.StatementService;
import com.gic.service.TransactionService;

import java.util.Scanner;

public class UIHelper {


    Scanner scanner = new Scanner(System.in);
    private final TransactionUI transactionUI;
    private final InterestRuleUI interestRuleUI;
    private final PrintStatementUI printStatementUI;

    public UIHelper(TransactionService transactionService,
                    InterestRuleService interestRuleService,
                    StatementService statementService) {
        this.transactionUI = new TransactionUI(scanner, transactionService);
        this.interestRuleUI = new InterestRuleUI(scanner, interestRuleService);
        this.printStatementUI = new PrintStatementUI(scanner, statementService);
    }

    public void mainLoop() {
        System.out.println("Welcome to AwesomeGIC Bank! What would you like to do?");

        while (true) {
            printMainMenu();
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            char choice = Character.toUpperCase(input.charAt(0));
            switch (choice) {
                case 'T':
                    transactionUI.start();
                    break;
                case 'I':
                    interestRuleUI.start();
                    break;
                case 'P':
                    printStatementUI.start();
                    break;
                case 'Q':
                    quit();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    System.out.println("Welcome to AwesomeGIC Bank! What would you like to do?");
            }
            System.out.println("Is there anything else you'd like to do?");
        }
    }

    private static void printMainMenu() {
        System.out.println("[T] Input transactions ");
        System.out.println("[I] Define interest rules");
        System.out.println("[P] Print statement");
        System.out.println("[Q] Quit");
        System.out.print(">");
    }

    private static void quit() {
        System.out.println("Thank you for banking with AwesomeGIC Bank.");
        System.out.println("Have a nice day!");
    }
}
