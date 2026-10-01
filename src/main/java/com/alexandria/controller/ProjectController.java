package com.alexandria.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.service.FileStorageService;
import com.alexandria.service.PdfService;
import com.alexandria.view.components.shared.document.highlight.TextPaginator;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;

public class ProjectController {

    private final TextDAO textDAO;
    private final PdfService pdfService;
    private final FileStorageService fileStorageService;
    private final UserSessionController session = UserSessionController.getInstance();

    public ProjectController(TextDAO textDAO, PdfService pdfService) {
        this(textDAO, pdfService, new FileStorageService());
    }

    ProjectController(TextDAO textDAO, PdfService pdfService, FileStorageService fileStorageService) {
        this.textDAO = textDAO;
        this.pdfService = pdfService;
        this.fileStorageService = fileStorageService;
    }

    public Result createProject(NewProjectModal.CreatedProject created) {
        try {
            String content;
            List<Integer> pageOffsets = List.of();
            File sourceFile = null;
            Path storedFile = null;

            if (created.sourceType() == NewProjectModal.SourceType.PASTE) {
                content = created.textContent();
            } else {
                File file = created.file();
                if (file == null)
                    throw new IllegalArgumentException("No file was selected.");

                sourceFile = file;
                String name = file.getName().toLowerCase();

                if (name.endsWith(".pdf")) {
                    PdfService.PagedText pagedText = pdfService.extractTextWithPageBoundaries(file);
                    content = pagedText.content();
                    pageOffsets = pagedText.pageOffsets();
                } else if (name.endsWith(".txt")) {
                    content = Files.readString(file.toPath());
                } else {
                    throw new IllegalArgumentException("Unsupported file type. Please upload a PDF or TXT file.");
                }
            }

            FileType fileType = resolveFileType(created);

            if (fileType != FileType.PDF) {
                pageOffsets = TextPaginator.paginate(content, TextPaginator.CHARS_PER_PAGE);
            }

            String fileName = resolveFileName(created);
            User user = session.getCurrentUser();

            // Guest: keep project in memory.
            if (user == null) {
                Text text = new Text(null, created.title(), fileName, fileType, content);
                return Result.ok(text, pageOffsets, sourceFile);
            }

            // Logged-in user: persist project.
            if (created.sourceType() == NewProjectModal.SourceType.UPLOAD) {
                storedFile = fileStorageService.saveFile(created.file(), user.getId());
            }

            Text text = new Text(
                    user.getId(),
                    created.title(),
                    fileName,
                    storedFile == null ? null : storedFile.toString(),
                    fileType,
                    content);
            Text persisted = textDAO.create(text);

            return Result.ok(persisted, pageOffsets, storedFile == null ? sourceFile : storedFile.toFile());

        } catch (Exception e) {
            return Result.error("Could not create project: " + e.getMessage());
        }
    }

    private FileType resolveFileType(NewProjectModal.CreatedProject created) {
        if (created.sourceType() == NewProjectModal.SourceType.PASTE)
            return FileType.MANUAL;

        File file = created.file();
        if (file == null)
            throw new IllegalArgumentException("No file was selected.");

        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf"))
            return FileType.PDF;
        if (name.endsWith(".txt"))
            return FileType.TXT;

        throw new IllegalArgumentException("Unsupported file type.");
    }

    private String resolveFileName(NewProjectModal.CreatedProject created) {
        if (created.sourceType() == NewProjectModal.SourceType.PASTE)
            return created.fileName();

        String provided = created.fileName();
        return provided != null && !provided.isBlank() ? provided : created.file().getName();
    }

    public record Result(boolean success, String message, Text text, List<Integer> pageOffsets, File sourceFile) {

        public static Result ok(Text text, List<Integer> pageOffsets, File sourceFile) {
            return new Result(true, null, text, pageOffsets, sourceFile);
        }

        public static Result error(String message) {
            return new Result(false, message, null, List.of(), null);
        }
    }
}