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
        Transaction transaction = transactionService.addTransaction("20240421", "ACC123", "D", "100.00");

        assertNotNull(transaction);
        assertEquals(DateUtil.parseDate("20240421"), transaction.getDate());
        assertEquals('D', transaction.getType());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());

        Account account = transactionService.getAccount("ACC123");
        assertNotNull(account);
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    public void testAddValidWithdrawalTransaction() {
        String dateStr = "20240421";
        String accountId = "ACC123";

        // Add a deposit first
        transactionService.addTransaction(dateStr, accountId, "D", "200.00");

        Transaction withdrawal = transactionService.addTransaction(dateStr, accountId, "W", "100.00");

        assertNotNull(withdrawal);
        assertEquals(DateUtil.parseDate(dateStr), withdrawal.getDate());
        assertEquals('W', withdrawal.getType());
        assertEquals(new BigDecimal("100.00"), withdrawal.getAmount());

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

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);
        });

        String expectedMessage = "First transaction cannot be a withdrawal.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // add a deposit first
        transactionService.addTransaction(dateStr, accountId, "D", "50.00");

        // test the second withdrawal
        exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.addTransaction(dateStr, accountId, typeStr, amountStr);
        });

        expectedMessage = "Withdrawal would cause negative balance.";
        actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddTransactionInvalidDate() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.addTransaction("2024-04-01", "ACC123", "D", "100.00"));
    }

    @Test
    void testAddTransactionEmptyAccount() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.addTransaction("20240401", "", "D", "100.00"));
    }

    @Test
    void testAddTransactionInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.addTransaction("20240401", "ACC123", "X", "100.00"));
    }

    @Test
    void testAddTransactionInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.addTransaction("20240401", "ACC123", "D", "abc"));
    }

    @Test
    void testAddTransactionWithdrawalBeforeDeposit() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.addTransaction("20240401", "ACC123", "W", "100.00"));
    }

    @Test
    void testAddTransactionWithdrawalExceedingBalance() {
        transactionService.addTransaction("20240401", "ACC123", "D", "100.00");
        assertThrows(IllegalArgumentException.class, () -> transactionService.addTransaction("20240402", "ACC123", "W", "200.00"));
    }

    @Test
    public void testGetAllTransactions() {
        transactionService.addTransaction("20240421", "ACC123", "D", "100.00");

        List<Transaction> transactions = transactionService.getAllTransactions("ACC123");
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    void testGetAllTransactionsNonExistentAccount() {
        List<Transaction> txns = transactionService.getAllTransactions("ACC123");
        assertTrue(txns.isEmpty());
    }

    @Test
    public void testGetTransactionsForMonth() {
        transactionService.addTransaction("20240421", "ACC123", "D", "100.00");
        transactionService.addTransaction("20240521", "ACC123", "D", "100.00");

        List<Transaction> transactions = transactionService.getTransactionsForMonth("ACC123", "202404");
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(DateUtil.parseDate("20240421"), transactions.get(0).getDate());
    }

    @Test
    void testGetAccount() {
        transactionService.getOrCreateAccount("ACC123");
        Account account = transactionService.getAccount("ACC123");
        assertNotNull(account);
    }

    @Test
    void testGetAccountNonExistent() {
        Account account = transactionService.getAccount("ACC123");
        assertNull(account);
    }
}
