package com.gic.service;

import com.gic.model.Account;
import com.gic.model.Transaction;
import com.gic.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddValidDepositTransaction() {
        String dateStr = "20240421";
        String accountId = "ACC123";
        String typeStr = "D";
        String amountStr = "100.00";

        Transaction transaction = transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);

        assertNotNull(transaction);
        assertEquals(DateUtil.parseDate(dateStr), transaction.getDate());
        assertEquals('D', transaction.getType());
        assertEquals(new BigDecimal(amountStr), transaction.getAmount());

        Account account = transactionService.getAccount(accountId);
        assertNotNull(account);
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    public void testAddValidWithdrawalTransaction() {
        String dateStr = "20240421";
        String accountId = "ACC123";
        String depositTypeStr = "D";
        String depositAmountStr = "200.00";
        String withdrawalTypeStr = "W";
        String withdrawalAmountStr = "100.00";

        // Add a deposit first
        transactionService.addTransaction(dateStr, accountId, depositTypeStr, depositAmountStr);

        Transaction withdrawal = transactionService.addTransaction(dateStr, accountId, withdrawalTypeStr, withdrawalAmountStr);

        assertNotNull(withdrawal);
        assertEquals(DateUtil.parseDate(dateStr), withdrawal.getDate());
        assertEquals('W', withdrawal.getType());
        assertEquals(new BigDecimal(withdrawalAmountStr), withdrawal.getAmount());

        Account account = transactionService.getAccount(accountId);
        assertNotNull(account);
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    public void testAddWithdrawalTransactionInsufficientBalance() {
        String dateStr = "20240421";
        String accountId = "ACC123";
        String typeStr = "W";
        String amountStr = "100.00";

        // Ensure that the function throws an exception if its the first transaction
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);
        });

        String expectedMessage = "First transaction cannot be a withdrawal.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // add a deposit first
        transactionService.addTransaction(dateStr, accountId, "D", "50.00");

        // Test the second withdrawal
        exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);
        });

        expectedMessage = "Withdrawal would cause negative balance.";
        actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testAddTransactionInvalidDateFormat() {
        String dateStr = "2024/04/21";
        String accountId = "ACC123";
        String typeStr = "D";
        String amountStr = "100.00";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);
        });

        String expectedMessage = "Invalid date format. Use YYYYMMdd.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testAddTransactionInvalidAmountFormat() {
        String dateStr = "20240421";
        String accountId = "ACC123";
        String typeStr = "D";
        String amountStr = "-100.00";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);
        });

        String expectedMessage = "Amount must be a positive number with up to 2 decimal places.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAllTransactions() {
        String dateStr = "20240421";
        String accountId = "ACC123";
        String typeStr = "D";
        String amountStr = "100.00";

        transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);

        List<Transaction> transactions = transactionService.getAllTransactions(accountId);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    public void testGetTransactionsForMonth() {
        String dateStr1 = "20240421";
        String dateStr2 = "20240521";
        String accountId = "ACC123";
        String typeStr = "D";
        String amountStr = "100.00";

        transactionService.addTransaction(dateStr1, accountId, typeStr, amountStr);
        transactionService.addTransaction(dateStr2, accountId, typeStr, amountStr);

        List<Transaction> transactions = transactionService.getTransactionsForMonth(accountId, "202404");
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(DateUtil.parseDate(dateStr1), transactions.get(0).getDate());
    }
}
