package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FormField;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;

public class FileValidatorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final FileValidator validator = new FileValidator();

    private final FormField imageField =
            new FormField("photo", "Photo", FormField.Type.IMG_FILE, false, java.util.List.of());
    private final FormField documentField =
            new FormField("document", "Document", FormField.Type.DOCUMENT_FILE, false, java.util.List.of());

    @Test
    public void acceptsValidImage() throws Exception {
        File file = tempFolder.newFile("avatar.png");
        assertNull(validator.validate(file.getAbsolutePath(), imageField));
    }

    @Test
    public void rejectsWrongImageExtension() throws Exception {
        File file = tempFolder.newFile("avatar.gif");
        assertNotNull(validator.validate(file.getAbsolutePath(), imageField));
    }

    @Test
    public void rejectsOversizedImage() throws Exception {
        File file = tempFolder.newFile("avatar.png");
        setFileSize(file, 6L * 1024 * 1024); // 6 MiB, over the 5 MiB image cap

        assertNotNull(validator.validate(file.getAbsolutePath(), imageField));
    }

    @Test
    public void acceptsValidDocument() throws Exception {
        File file = tempFolder.newFile("notes.txt");
        assertNull(validator.validate(file.getAbsolutePath(), documentField));
    }

    @Test
    public void rejectsWrongDocumentExtension() throws Exception {
        File file = tempFolder.newFile("notes.docx");
        assertNotNull(validator.validate(file.getAbsolutePath(), documentField));
    }

    @Test
    public void rejectsNonexistentFile() {
        String missingPath = tempFolder.getRoot().getAbsolutePath() + "/does-not-exist.pdf";
        assertNotNull(validator.validate(missingPath, documentField));
    }

    /** Grows a file to an exact size instantly (sparse file) — no need to actually write 6MB of bytes. */
    private void setFileSize(File file, long size) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.setLength(size);
        }
    }
}
