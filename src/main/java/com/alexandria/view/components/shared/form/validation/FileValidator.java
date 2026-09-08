package com.alexandria.view.components.shared.form.validation;

import java.io.File;
import java.util.Locale;

import com.alexandria.view.components.shared.form.FieldValidator;
import com.alexandria.view.components.shared.form.FormField;

public final class FileValidator implements FieldValidator {

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final long MAX_DOCUMENT_SIZE = 50L * 1024 * 1024;

    @Override
    public String validate(String value, FormField field) {
        File file = new File(value);

        return switch (field.type()) {
            case IMG_FILE -> validateImage(field, file);
            case DOCUMENT_FILE -> validateDocument(field, file);
            default -> null;
        };
    }

    private String validateImage(FormField field, File file) {
        if (!isFileValid(file, MAX_IMAGE_SIZE, ".png", ".jpg", ".jpeg")) {
            return field.label() + " must be a PNG, JPG, or JPEG image no larger than 5 MiB.";
        }
        return null;
    }

    private String validateDocument(FormField field, File file) {
        if (!isFileValid(file, MAX_DOCUMENT_SIZE, ".pdf", ".txt")) {
            return field.label() + " must be a PDF or TXT file no larger than 50 MiB.";
        }
        return null;
    }

    private boolean isFileValid(File file, long maxSize, String... extensions) {
        if (file == null || !file.isFile() || file.length() > maxSize) {
            return false;
        }

        String fileName = file.getName().toLowerCase(Locale.ROOT);

        for (String extension : extensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }
}