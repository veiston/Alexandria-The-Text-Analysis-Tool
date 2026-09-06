package com.alexandria.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordValidatorTest {

    @Test
    public void rejectsNull() {
        assertFalse(PasswordValidator.isValid(null));
    }

    @Test
    public void rejectsTooShort() {
        assertFalse(PasswordValidator.isValid("short7"));
    }

    @Test
    public void acceptsExactlyMinLength() {
        assertTrue(PasswordValidator.isValid("exactly8"));
    }

    @Test
    public void acceptsLongPassword() {
        assertTrue(PasswordValidator.isValid("a-fairly-long-password"));
    }
}
