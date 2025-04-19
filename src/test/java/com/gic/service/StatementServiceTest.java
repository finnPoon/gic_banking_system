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
import java.util.List;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPrintStatementAccountNotFound() {
        // Arrange
        String accountId = "ACC123";
        String yearMonth = "202404";

        when(transactionService.getAccount(accountId)).thenReturn(null);

        // Act
        statementService.printStatement(accountId, yearMonth);

        // Assert
        verify(transactionService, times(1)).getAccount(accountId);
        verify(transactionService, never()).getTransactionsForMonth(anyString(), anyString());
        verify(interestRuleService, never()).getRulesBetween(any(), any());
    }
}
