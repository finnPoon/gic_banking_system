package com.gic.service;

import com.gic.model.InterestRule;
import com.gic.util.DateUtil;
import com.gic.util.InputUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class InterestRuleService {

    private final TreeMap<LocalDate, InterestRule> interestRules = new TreeMap<>();

    public synchronized void addOrUpdateInterestRule(String dateStr, String ruleId, String rateStr) throws IllegalArgumentException {
        LocalDate date = DateUtil.parseDate(dateStr);
        if (date == null) {
            throw new IllegalArgumentException("Invalid date format. Use YYYYMMdd.");
        }
        if (ruleId == null || ruleId.isEmpty()) {
            throw new IllegalArgumentException("RuleId cannot be empty.");
        }
        if (!InputUtil.isValidRate(rateStr)) {
            throw new IllegalArgumentException("Rate must be >0 and <100.");
        }
        BigDecimal rate = new BigDecimal(rateStr);

        InterestRule rule = new InterestRule(date, ruleId, rate);
        // if there is existing rule on same date, replace
        interestRules.put(date, rule);
    }

    public synchronized List<InterestRule> getAllRules() {
        return new ArrayList<>(interestRules.values());
    }

    public synchronized InterestRule getRuleForDate(LocalDate date) {
        Map.Entry<LocalDate, InterestRule> entry = interestRules.floorEntry(date);
        return entry != null ? entry.getValue() : null;
    }

    public synchronized NavigableMap<LocalDate, InterestRule> getRulesBetween(LocalDate start, LocalDate end) {
        return interestRules.subMap(start, true, end, true);
    }

    public synchronized LocalDate getEarliestRuleDate() {
        return interestRules.isEmpty() ? null : interestRules.firstKey();
    }
}
