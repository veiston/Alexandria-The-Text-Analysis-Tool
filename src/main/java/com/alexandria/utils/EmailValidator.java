package com.alexandria.utils;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}