package com.alexandria.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileStorageService {
    private final Path uploadsDirectory;

    public FileStorageService() {
        this(Path.of(System.getProperty("alexandria.uploads.dir", "uploads")));
    }

    public FileStorageService(Path uploadsDirectory) {
        this.uploadsDirectory = uploadsDirectory;
    }

    public Path saveFile(File sourceFile, int userId) throws IOException {
        String fileName = sourceFile.getName();
        String extension = "";
        int extensionStart = fileName.lastIndexOf('.');

        if (extensionStart >= 0) {
            extension = fileName.substring(extensionStart);
        }

        Path path = uploadsDirectory.resolve(String.valueOf(userId)).resolve(UUID.randomUUID() + extension);

        Files.createDirectories(path.getParent());

        return Files.copy(sourceFile.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
    }

    public File getFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return null;
        }

        try {
            Path path = Path.of(filePath).normalize();
            return Files.isRegularFile(path) && Files.isReadable(path) ? path.toFile() : null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public void deleteFile(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            return;
        }
        Files.deleteIfExists(Path.of(filePath).normalize());
    }
}
