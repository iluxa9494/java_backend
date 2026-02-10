package com.skillbox.cryptobot.utils;

import java.math.BigDecimal;

public class TextUtil {

    /**
     * Форматирует число с 3 знаками после запятой.
     *
     * @param value Число в BigDecimal
     * @return Строка с округленным числом
     */
    public static String toString(BigDecimal value) {
        return String.format("%.3f", value);
    }
}
