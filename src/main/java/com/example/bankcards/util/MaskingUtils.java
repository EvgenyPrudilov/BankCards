package com.example.bankcards.util;

public class MaskingUtils {

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }

        String clean = cardNumber.replaceAll("\\s+", "");
        if (clean.length() < 4) {
            return "****";
        }

        String lastFour = clean.substring(clean.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
