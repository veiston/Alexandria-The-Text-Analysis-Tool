package com.alexandria.view.components.shared.form;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormValidatorTest {

    @Mock
    private FormRenderer renderer;

    @Test
    public void requiredEmptyFieldFailsWithoutCallingItsValidator() {
        FieldValidator mockValidator = mock(FieldValidator.class);
        FormField field = new FormField("name", "Name", FormField.Type.TEXT, true, List.of(mockValidator));

        when(renderer.isEmpty(field)).thenReturn(true);

        FormValidator validator = new FormValidator(null);
        String error = validator.validate(List.of(field), renderer);

        assertEquals("Name is required.", error);
        verify(mockValidator, never()).validate(any(), any());
    }

    @Test
    public void optionalEmptyFieldPasses() {
        FormField field = new FormField("nickname", "Nickname", FormField.Type.TEXT, false, List.of());
        when(renderer.isEmpty(field)).thenReturn(true);

        FormValidator validator = new FormValidator(null);
        assertNull(validator.validate(List.of(field), renderer));
    }

    @Test
    public void fieldValidatorErrorIsReturned() {
        FieldValidator failing = mock(FieldValidator.class);
        FormField field = new FormField("email", "Email", FormField.Type.TEXT, true, List.of(failing));

        when(renderer.isEmpty(field)).thenReturn(false);
        when(renderer.getValue(field)).thenReturn("not-an-email");
        when(failing.validate("not-an-email", field)).thenReturn("Please enter a valid email address.");

        FormValidator validator = new FormValidator(null);
        String error = validator.validate(List.of(field), renderer);

        assertEquals("Please enter a valid email address.", error);
    }

    @Test
    public void stopsAtFirstFailingFieldAndSkipsLaterOnes() {
        FieldValidator firstValidator = mock(FieldValidator.class);
        FieldValidator secondValidator = mock(FieldValidator.class);

        FormField first = new FormField("a", "A", FormField.Type.TEXT, true, List.of(firstValidator));
        FormField second = new FormField("b", "B", FormField.Type.TEXT, true, List.of(secondValidator));

        when(renderer.isEmpty(first)).thenReturn(false);
        when(renderer.getValue(first)).thenReturn("value-a");
        when(firstValidator.validate("value-a", first)).thenReturn("A is invalid.");

        FormValidator validator = new FormValidator(null);
        String error = validator.validate(List.of(first, second), renderer);

        assertEquals("A is invalid.", error);
        verify(secondValidator, never()).validate(any(), any());
    }

    @Test
    public void customValidatorRunsAfterAllFieldsPass() {
        FormField field = new FormField("a", "A", FormField.Type.TEXT, true, List.of());
        when(renderer.isEmpty(field)).thenReturn(false);
        when(renderer.getValue(field)).thenReturn("ok");
        when(renderer.getValues()).thenReturn(Map.of("a", "ok"));

        FormValidator validator = new FormValidator(values -> "Custom rule failed.");
        String error = validator.validate(List.of(field), renderer);

        assertEquals("Custom rule failed.", error);
    }

    @Test
    public void customValidatorNotCalledIfAFieldAlreadyFailed() {
        FieldValidator failing = mock(FieldValidator.class);
        FormField field = new FormField("a", "A", FormField.Type.TEXT, true, List.of(failing));

        when(renderer.isEmpty(field)).thenReturn(false);
        when(renderer.getValue(field)).thenReturn("bad");
        when(failing.validate("bad", field)).thenReturn("A is invalid.");

        boolean[] customValidatorCalled = { false };
        FormValidator validator = new FormValidator(values -> {
            customValidatorCalled[0] = true;
            return null;
        });

        validator.validate(List.of(field), renderer);

        assertFalse(customValidatorCalled[0]);
    }
}
