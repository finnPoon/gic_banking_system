package com.gic.service;

import com.gic.model.InterestRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NavigableMap;

import static org.junit.jupiter.api.Assertions.*;

public class InterestRuleServiceTest {

    @InjectMocks
    private InterestRuleService interestRuleService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddOrUpdateInterestRule() {
        String dateStr = "20240421";
        String ruleId = "RULE001";
        String rateStr = "1.50";

        interestRuleService.addOrUpdateInterestRule(dateStr, ruleId, rateStr);

        List<InterestRule> rules = interestRuleService.getAllRules();
        assertNotNull(rules);
        assertEquals(1, rules.size());
        assertEquals(ruleId, rules.get(0).getRuleId());
    }

    @Test
    void testAddOrUpdateInterestRuleInvalidDate() {
        assertThrows(IllegalArgumentException.class, () -> interestRuleService.addOrUpdateInterestRule("2024-05-03", "RULE001", "1.5"));
    }

    @Test
    void testAddOrUpdateInterestRuleEmptyRuleId() {
        assertThrows(IllegalArgumentException.class, () -> interestRuleService.addOrUpdateInterestRule("20240503", "", "1.5"));
    }

    @Test
    void testAddOrUpdateInterestRuleInvalidRate() {
        assertThrows(IllegalArgumentException.class, () -> interestRuleService.addOrUpdateInterestRule("20240503", "RULE001", "101"));
    }

    @Test
    public void testGetAllRules() {
        String dateStr1 = "20240421";
        String ruleId1 = "RULE001";
        String rateStr1 = "1.50";
        String dateStr2 = "20240422";
        String ruleId2 = "RULE002";
        String rateStr2 = "2.00";

        interestRuleService.addOrUpdateInterestRule(dateStr1, ruleId1, rateStr1);
        interestRuleService.addOrUpdateInterestRule(dateStr2, ruleId2, rateStr2);

        List<InterestRule> rules = interestRuleService.getAllRules();
        assertNotNull(rules);
        assertEquals(2, rules.size());
        assertEquals(ruleId1, rules.get(0).getRuleId());
        assertEquals(ruleId2, rules.get(1).getRuleId());
    }

    @Test
    void testGetRuleForDate() {
        interestRuleService.addOrUpdateInterestRule("20240503", "RULE001", "1.5");
        InterestRule rule = interestRuleService.getRuleForDate(LocalDate.of(2024, 5, 3));
        assertNotNull(rule);
        assertEquals(LocalDate.of(2024, 5, 3), rule.getDate());
        assertEquals("RULE001", rule.getRuleId());
        assertEquals(new BigDecimal("1.5"), rule.getRate());

        rule = interestRuleService.getRuleForDate(LocalDate.of(2024, 5, 4));
        assertNotNull(rule);
        assertEquals(LocalDate.of(2024, 5, 3), rule.getDate());
        assertEquals("RULE001", rule.getRuleId());
        assertEquals(new BigDecimal("1.5"), rule.getRate());

        rule = interestRuleService.getRuleForDate(LocalDate.of(2024, 5, 2));
        assertNull(rule);
    }

    @Test
    void testGetRulesBetween() {
        interestRuleService.addOrUpdateInterestRule("20240501", "RULE001", "1.0");
        interestRuleService.addOrUpdateInterestRule("20240503", "RULE002", "1.5");
        interestRuleService.addOrUpdateInterestRule("20240505", "RULE003", "2.0");

        NavigableMap<LocalDate, InterestRule> rules = interestRuleService.getRulesBetween(LocalDate.of(2024, 5, 2), LocalDate.of(2024, 5, 4));
        assertEquals(1, rules.size());
        assertTrue(rules.containsKey(LocalDate.of(2024, 5, 3)));
    }
}
