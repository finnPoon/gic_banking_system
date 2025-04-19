package com.gic.service;

import com.gic.model.Account;
import com.gic.model.Transaction;
import com.gic.util.DateUtil;
import com.gic.util.InputUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionService {

    // thread-safe map of accounts
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    public Account getOrCreateAccount(String accountId) {
        return accounts.computeIfAbsent(accountId, Account::new);
    }

    public Transaction addTransaction(String dateStr, String accountId, String typeStr, String amountStr) throws IllegalArgumentException {
        LocalDate date = DateUtil.parseDate(dateStr);
        if (date == null) {
            throw new IllegalArgumentException("Invalid date format. Use YYYYMMdd.");
        }
        if (accountId == null || accountId.isEmpty()) {
            throw new IllegalArgumentException("Account cannot be empty.");
        }
        if (typeStr == null || typeStr.length() != 1) {
            throw new IllegalArgumentException("Transaction type must be 'D' or 'W'.");
        }
        char type = Character.toUpperCase(typeStr.charAt(0));
        if (type != 'D' && type != 'W') {
            throw new IllegalArgumentException("Transaction type must be 'D' or 'W'.");
        }
        if (!InputUtil.isValidAmount(amountStr)) {
            throw new IllegalArgumentException("Amount must be a positive number with up to 2 decimal places.");
        }
        BigDecimal amount = new BigDecimal(amountStr);

        Account account = getOrCreateAccount(accountId);
        ReentrantReadWriteLock.WriteLock writeLock = account.getLock().writeLock();
        writeLock.lock();
        try {
            // validate balance constraints
            BigDecimal currentBalance = account.getBalance();
            if (account.getAllTransactions().isEmpty()) {
                // first transaction cannot be withdrawal
                if (type == 'W') {
                    throw new IllegalArgumentException("First transaction cannot be a withdrawal.");
                }
            }
            if (type == 'W' && currentBalance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("Withdrawal would cause negative balance.");
            }
            return account.addTransaction(date, type, amount);
        } finally {
            writeLock.unlock();
        }
    }

    public List<Transaction> getAllTransactions(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) return List.of();
        return account.getAllTransactions();
    }

    public List<Transaction> getTransactionsForMonth(String accountId, String yearMonth) {
        Account account = accounts.get(accountId);
        if (account == null) return List.of();
        return account.getTransactionsForMonth(yearMonth);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }
}
