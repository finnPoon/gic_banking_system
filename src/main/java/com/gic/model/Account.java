package com.gic.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Account {
    private final String accountId;
    // transactions stored in TreeMap keyed by date, each date maps to list of transactions
    private final TreeMap<LocalDate, List<Transaction>> transactionsByDate = new TreeMap<>();
    // map to track running number per date for transaction ID generation
    private final Map<LocalDate, Integer> txnCountPerDate = new HashMap<>();
    // current balance
    private BigDecimal balance = BigDecimal.ZERO;

    public Account(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public Transaction addTransaction(LocalDate date, char type, BigDecimal amount) {
        int seq = txnCountPerDate.getOrDefault(date, 0) + 1;
        txnCountPerDate.put(date, seq);
        String txnId = String.format("%s-%02d", date.toString().replaceAll("-", ""), seq);
        Transaction txn = new Transaction(txnId, date, type, amount);

        transactionsByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(txn);

        if (type == 'D' || type == 'I') {
            balance = balance.add(amount);
        } else if (type == 'W') {
            balance = balance.subtract(amount);
        }

        return txn;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> all = new ArrayList<>();
        for (List<Transaction> txns : transactionsByDate.values()) {
            all.addAll(txns);
        }
        return all;
    }

    public List<Transaction> getTransactionsForMonth(String yearMonth) {
        List<Transaction> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Transaction>> entry : transactionsByDate.subMap(
                LocalDate.parse(yearMonth + "01", java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                true,
                LocalDate.parse(yearMonth + "31", java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                true).entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

    public BigDecimal getBalanceAtDate(LocalDate date) {
        BigDecimal bal = BigDecimal.ZERO;
        for (Map.Entry<LocalDate, List<Transaction>> entry : transactionsByDate.headMap(date, true).entrySet()) {
            for (Transaction txn : entry.getValue()) {
                if (txn.getType() == 'D' || txn.getType() == 'I') {
                    bal = bal.add(txn.getAmount());
                } else if (txn.getType() == 'W') {
                    bal = bal.subtract(txn.getAmount());
                }
            }
        }
        return bal;
    }
}
