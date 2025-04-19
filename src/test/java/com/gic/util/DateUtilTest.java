package com.gic.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest {

    @Test
    public void testParseValidDate() {
        String dateStr = "20240421";
        LocalDate date = DateUtil.parseDate(dateStr);
        assertNotNull(date);
        assertEquals(LocalDate.of(2024, 4, 21), date);
    }

    @Test
    public void testParseInvalidDate() {
        String dateStr = "2024/04/21";
        LocalDate date = DateUtil.parseDate(dateStr);
        assertNull(date);
    }
}
