package com.gic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InterestRule implements Comparable<InterestRule> {
    private final LocalDate date;
    private final String ruleId;
    private final BigDecimal rate;

    public InterestRule(LocalDate date, String ruleId, BigDecimal rate) {
        this.date = date;
        this.ruleId = ruleId;
        this.rate = rate;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRuleId() {
        return ruleId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public int compareTo(InterestRule other) {
        return this.date.compareTo(other.date);
    }
}
