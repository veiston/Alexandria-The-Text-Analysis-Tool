package com.alexandria.controller;

import java.io.File;
import java.nio.file.Files;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.service.PdfService;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;

public class ProjectController {

    private final TextDAO textDAO;
    private final PdfService pdfService;

    private final UserSessionController session = UserSessionController.getInstance();

    public ProjectController(
            TextDAO textDAO,
            PdfService pdfService) {

        this.textDAO = textDAO;
        this.pdfService = pdfService;
    }

    public Result createProject(
            NewProjectModal.CreatedProject created) {

        try {
            String content = extractContent(created);
            FileType fileType = resolveFileType(created);
            String fileName = resolveFileName(created);

            User user = session.getCurrentUser();

            // Guest: keep project in memory.
            if (user == null) {
                Text text = new Text(
                        null,
                        created.title(),
                        fileName,
                        fileType,
                        content);

                return Result.ok(text);
            }

            // Logged-in user: persist project.
            Text text = new Text(
                    user.getId(),
                    created.title(),
                    fileName,
                    fileType,
                    content);

            Text persisted = textDAO.create(text);

            return Result.ok(persisted);

        } catch (Exception e) {
            return Result.error(
                    "Could not create project: " + e.getMessage());
        }
    }

    private String extractContent(
            NewProjectModal.CreatedProject created)
            throws Exception {

        if (created.sourceType() == NewProjectModal.SourceType.PASTE) {
            return created.textContent();
        }

        File file = created.file();

        if (file == null) {
            throw new IllegalArgumentException(
                    "No file was selected.");
        }

        String name = file.getName().toLowerCase();

        if (name.endsWith(".pdf")) {
            return pdfService.extractText(file);
        }

        if (name.endsWith(".txt")) {
            return Files.readString(file.toPath());
        }

        throw new IllegalArgumentException(
                "Unsupported file type. Please upload a PDF or TXT file.");
    }

    private FileType resolveFileType(
            NewProjectModal.CreatedProject created) {

        if (created.sourceType() == NewProjectModal.SourceType.PASTE) {
            return FileType.MANUAL;
        }

        File file = created.file();

        if (file == null) {
            throw new IllegalArgumentException(
                    "No file was selected.");
        }

        String name = file.getName().toLowerCase();

        if (name.endsWith(".pdf")) {
            return FileType.PDF;
        }

        if (name.endsWith(".txt")) {
            return FileType.TXT;
        }

        throw new IllegalArgumentException(
                "Unsupported file type.");
    }

    private String resolveFileName(
            NewProjectModal.CreatedProject created) {

        if (created.sourceType() == NewProjectModal.SourceType.PASTE) {
            return created.fileName();
        }

        String provided = created.fileName();

        return provided != null && !provided.isBlank()
                ? provided
                : created.file().getName();
    }

    public record Result(
            boolean success,
            String message,
            Text text) {

        public static Result ok(Text text) {
            return new Result(true, null, text);
        }

        public static Result error(String message) {
            return new Result(false, message, null);
        }
    }
}