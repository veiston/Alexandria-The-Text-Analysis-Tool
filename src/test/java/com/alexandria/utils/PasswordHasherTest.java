package com.alexandria.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordHasherTest {

    @Test
    public void hashedPasswordMatchesOriginal() {
        String hash = PasswordHasher.hash("mySecret123");
        assertTrue(PasswordHasher.matches("mySecret123", hash));
    }

    @Test
    public void wrongPasswordDoesNotMatch() {
        String hash = PasswordHasher.hash("mySecret123");
        assertFalse(PasswordHasher.matches("wrongPassword", hash));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hashRejectsNull() {
        PasswordHasher.hash(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hashRejectsEmpty() {
        PasswordHasher.hash("");
    }

    @Test
    public void matchesReturnsFalseForNullInputs() {
        assertFalse(PasswordHasher.matches(null, "somehash"));
        assertFalse(PasswordHasher.matches("plain", null));
    }
}