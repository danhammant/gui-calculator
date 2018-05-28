package com.guicalculator.logic;

import java.math.BigDecimal;

public final class CalculatorLogic {

    public static double add(double a, double b) {
        return a + b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double divide(double a, double b) {
        return a / b;
    }

    //Need to look at this in more detail, probably not great code, but it solves the problem of the binary
    //representation of certain numbers (3, 6 and 7 were found to be problematic).
    public static double percent(double number, double percent) {
        double percentage = number * (percent / 100.0);
        String roundedPercentage = BigDecimal.valueOf(percentage).setScale(10, BigDecimal.ROUND_HALF_UP).toString();
        return Double.parseDouble(roundedPercentage);
    }
}
