package com.gic.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InputUtilTest {

    @Test
    public void testIsValidAmountValidAmount() {
        String amountStr = "100.00";
        assertTrue(InputUtil.isValidAmount(amountStr));
    }

    @Test
    public void testIsValidAmountInvalidAmount() {
        String amountStr = "-100.00";
        assertFalse(InputUtil.isValidAmount(amountStr));
    }

    @Test
    public void testIsValidRateValidRate() {
        String rateStr = "1.50";
        assertTrue(InputUtil.isValidRate(rateStr));
    }

    @Test
    public void testIsValidRateInvalidRate() {
        String rateStr = "-1.50";
        assertFalse(InputUtil.isValidRate(rateStr));
    }
}
