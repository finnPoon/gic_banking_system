package com.gic.service;

import com.gic.model.InterestRule;
import com.gic.util.DateUtil;
import com.gic.util.InputUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class InterestRuleService {

    private final TreeMap<LocalDate, InterestRule> interestRules = new TreeMap<>();

    public void addOrUpdateInterestRule(String dateStr, String ruleId, String rateStr) throws IllegalArgumentException {
        LocalDate date = DateUtil.parseDate(dateStr);
        if (date == null) {
            throw new IllegalArgumentException("Invalid date format. Use YYYYMMdd.");
        }
        if (ruleId == null || ruleId.isEmpty()) {
            throw new IllegalArgumentException("RuleId cannot be empty.");
        }
        if (!InputUtil.isValidRate(rateStr)) {
            throw new IllegalArgumentException("Rate must be more than 0 and less than 100.");
        }
        BigDecimal rate = new BigDecimal(rateStr);

        InterestRule rule = new InterestRule(date, ruleId, rate);
        // if there is existing rule on same date, replace old one with the latest one
        interestRules.put(date, rule);
    }

    public List<InterestRule> getAllRules() {
        return new ArrayList<>(interestRules.values());
    }

    public InterestRule getRuleForDate(LocalDate date) {
        Map.Entry<LocalDate, InterestRule> entry = interestRules.floorEntry(date);
        return entry != null ? entry.getValue() : null;
    }

    public NavigableMap<LocalDate, InterestRule> getRulesBetween(LocalDate start, LocalDate end) {
        return interestRules.subMap(start, true, end, true);
    }

    public LocalDate getEarliestRuleDate() {
        return interestRules.isEmpty() ? null : interestRules.firstKey();
    }
}
