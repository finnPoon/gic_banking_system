package com.gic.util;

import java.math.BigDecimal;

public class InputUtil {

    //validate if the number is a positive number and has no more than 2 decimal places
    public static boolean isValidAmount(String s) {
        try {
            BigDecimal bd = new BigDecimal(s);
            if (bd.scale() > 2) return false;
            return bd.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    //validate if the number is a valid number, greater than 0 and less than 100
    public static boolean isValidRate(String s) {
        try {
            BigDecimal bd = new BigDecimal(s);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) return false;
            if (bd.compareTo(new BigDecimal("100")) >= 0) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
