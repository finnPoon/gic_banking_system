package com.gic.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

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

    @Test
    public void testParseValidYearMonth() {
        String dateStr = "202404";
        YearMonth ym = DateUtil.parseYearMonth(dateStr);
        assertNotNull(ym);
        assertEquals(YearMonth.of(2024, 4), ym);
    }

    @Test
    public void testParseInvalidYearMonth() {
        String dateStr = "2024/04";
        YearMonth ym = DateUtil.parseYearMonth(dateStr);
        assertNull(ym);
    }
}
