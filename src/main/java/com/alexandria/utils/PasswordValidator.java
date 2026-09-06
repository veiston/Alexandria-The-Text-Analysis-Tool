package com.alexandria.utils;

public class PasswordValidator {
    private static final int MIN_LENGTH = 8;

    public static boolean isValid(String password) {
        return password != null && password.length() >= MIN_LENGTH;
    }
}
