package com.skillbox.cryptobot.utils;

import java.math.BigDecimal;

public class TextUtil {

    public static String toString(BigDecimal value) {
        return String.format("%.3f", value);
    }
}
