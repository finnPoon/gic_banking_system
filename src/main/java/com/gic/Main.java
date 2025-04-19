package com.gic;

import com.gic.service.InterestRuleService;
import com.gic.service.StatementService;
import com.gic.service.TransactionService;
import com.gic.ui.UIHelper;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TransactionService transactionService = new TransactionService();
        InterestRuleService interestRuleService = new InterestRuleService();
        StatementService statementService = new StatementService(transactionService, interestRuleService);

        UIHelper uiHelper = new UIHelper(transactionService, interestRuleService, statementService);
        uiHelper.mainLoop();
    }
}
