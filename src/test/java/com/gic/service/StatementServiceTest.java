package com.gic.service;

import com.gic.model.Account;
import com.gic.model.InterestRule;
import com.gic.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StatementServiceTest {

    @InjectMocks
    private StatementService statementService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private InterestRuleService interestRuleService;

    @Mock
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPrintStatement() {
        String accountId = "ACC123";
        String yearMonth = "202404";

        InterestRule interestRule = new InterestRule(LocalDate.of(2024, 1, 1), "RULE001", new BigDecimal("1.00"));
        TreeMap<LocalDate, InterestRule> interestRulesMap = new TreeMap<>();
        interestRulesMap.put(LocalDate.of(2024, 1, 1), interestRule);

        when(transactionService.getAccount(accountId)).thenReturn(account);
        when(account.getTransactionsForMonth(yearMonth)).thenReturn(new ArrayList<>());
        when(account.getBalanceAtDate(any(LocalDate.class))).thenReturn(BigDecimal.ZERO);
        when(interestRuleService.getRulesBetween(any(), any())).thenReturn(interestRulesMap);
        when(interestRuleService.getRuleForDate(any(LocalDate.class))).thenReturn(interestRule);

        statementService.printStatement(accountId, yearMonth);

        verify(transactionService, times(1)).getAccount(accountId);
        verify(interestRuleService, times(1)).getRulesBetween(any(), any());
        verify(interestRuleService, atLeastOnce()).getRuleForDate(any(LocalDate.class));
    }

    @Test
    void testPrintStatementAccountNotFound() {
        String accountId = "ACC123";
        String yearMonth = "202404";

        when(transactionService.getAccount(accountId)).thenReturn(null);

        statementService.printStatement(accountId, yearMonth);

        verify(transactionService, times(1)).getAccount(accountId);
        verify(transactionService, never()).getTransactionsForMonth(anyString(), anyString());
        verify(interestRuleService, never()).getRulesBetween(any(), any());
    }
}
