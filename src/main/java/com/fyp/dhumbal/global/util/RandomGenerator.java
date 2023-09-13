package com.fyp.dhumbal.global.util;

import java.util.Random;

public class RandomGenerator {

    private RandomGenerator() {
    }

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";

    public static String generateAlphanumeric(int length) {
        return generateRandom(NUMBERS + ALPHABET.toUpperCase() + NUMBERS, length);
    }

    public static String generateAlphabetic(int length) {
        return generateRandom(ALPHABET.toUpperCase(), length);

    }

    public static String generateNumeric(int length) {
        return generateRandom(NUMBERS, length);
    }

    private static String generateRandom(String seed, int length) {
        Random random = new Random();
        StringBuilder ran = new StringBuilder();
        while (length > 0) {
            ran.append(seed.charAt(random.nextInt(seed.length())));
            length--;
        }
        return ran.toString();
    }
}
