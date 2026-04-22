package com.framework.utils;

import java.util.Random;

public class DataGenerator {

    private static final Random rand = new Random();

    private static final String[] FIRST_NAMES = {
            "James", "Oliver", "Emma", "Sophia", "Liam",
            "Ava", "Noah", "Isabella", "Ethan", "Mia"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones",
            "Garcia", "Miller", "Davis", "Wilson", "Taylor"
    };

    private DataGenerator() {
    }

    public static String firstName() {
        return FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)];
    }

    public static String lastName() {
        return LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
    }

    public static String zipCode() {
        return String.format("%05d", rand.nextInt(90000) + 10000);
    }
}