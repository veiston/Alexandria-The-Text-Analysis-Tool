package com.alexandria.view.components.shared.form;

/**
 * Validates one field's value once it's known to be non-empty
 */
public interface FieldValidator {
	String validate(String value, FormField field);
}
