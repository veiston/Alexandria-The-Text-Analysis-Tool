package com.alexandria.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmailValidatorTest {

    @Test
    public void acceptsValidEmail() {
        assertTrue(EmailValidator.isValid("user@example.com"));
    }

    @Test
    public void rejectsMissingAtSign() {
        assertFalse(EmailValidator.isValid("userexample.com"));
    }

    @Test
    public void rejectsMissingDomain() {
        assertFalse(EmailValidator.isValid("user@"));
    }

    @Test
    public void rejectsNull() {
        assertFalse(EmailValidator.isValid(null));
    }
}