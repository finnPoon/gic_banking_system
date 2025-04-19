package com.gic.service;

import com.gic.model.InterestRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
}
