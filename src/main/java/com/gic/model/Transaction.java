package com.gic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private final String txnId;
    private final LocalDate date;
    private final char type;
    private final BigDecimal amount;

    public Transaction(String txnId, LocalDate date, char type, BigDecimal amount) {
        this.txnId = txnId;
        this.date = date;
        this.type = Character.toUpperCase(type);
        this.amount = amount;
    }

    public String getTxnId() {
        return txnId;
    }

    public LocalDate getDate() {
        return date;
    }

    public char getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
