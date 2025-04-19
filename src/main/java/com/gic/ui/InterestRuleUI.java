package com.gic.ui;

import com.gic.model.InterestRule;
import com.gic.service.InterestRuleService;

import java.util.List;
import java.util.Scanner;

public class InterestRuleUI {

    private final Scanner scanner;
    private final InterestRuleService interestRuleService;

    public InterestRuleUI(Scanner scanner, InterestRuleService interestRuleService) {
        this.scanner = scanner;
        this.interestRuleService = interestRuleService;
    }

    public void start() {
        System.out.println("\nPlease enter interest rules details in <Date> <RuleId> <Rate in %> format ");
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
        if (parts.length != 3) {
            System.out.println("Invalid input format. Please enter exactly 3 fields.");
            return;
        }
        try {
            interestRuleService.addOrUpdateInterestRule(parts[0], parts[1], parts[2]);
            printInterestRules();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printInterestRules() {
        List<InterestRule> rules = interestRuleService.getAllRules();
        System.out.println("\nInterest rules:");
        System.out.println("| Date     | RuleId | Rate (%) |");
        for (InterestRule rule : rules) {
            System.out.printf("| %s | %-6s | %8.2f |\n",
                    rule.getDate().toString().replaceAll("-", ""),
                    rule.getRuleId(),
                    rule.getRate());
        }
    }
}
