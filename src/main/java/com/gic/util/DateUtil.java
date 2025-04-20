package com.gic.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");

    public static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static YearMonth formatYearMonth(String yearMonth) {
        try {
            return YearMonth.parse(yearMonth, YEAR_MONTH_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}
