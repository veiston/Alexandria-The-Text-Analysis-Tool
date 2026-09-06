package com.alexandria.utils;

import com.password4j.Argon2Function;
import com.password4j.Password;

public final class PasswordHasher {

    private static final int MEMORY = 19456; // 19 MiB
    private static final int ITERATIONS = 2;
    private static final int PARALLELISM = 1;
    private static final int HASH_LENGTH = 32;

    /**
     * Hashes a password using Argon2id.
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        Argon2Function argon2 = Argon2Function.getInstance(
                MEMORY,
                ITERATIONS,
                PARALLELISM,
                HASH_LENGTH,
                com.password4j.types.Argon2.ID);

        return Password.hash(plainPassword).with(argon2).getResult();
    }

    /**
     * Checks a plain-text password against a previously generated Argon2id hash.
     */
    public static boolean matches(
            String plainPassword,
            String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        Argon2Function argon2 = Argon2Function.getInstance(
                MEMORY,
                ITERATIONS,
                PARALLELISM,
                HASH_LENGTH,
                com.password4j.types.Argon2.ID);

        return Password.check(plainPassword, hashedPassword)
                .with(argon2);
    }
}
