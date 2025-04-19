package com.gic.util;

import java.time.LocalDate;
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

    public static LocalDate parseYearMonth(String s) {
        try {
            return LocalDate.parse(s + "01", DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static String formatYearMonth(LocalDate date) {
        return date.format(YEAR_MONTH_FORMAT);
    }
}
