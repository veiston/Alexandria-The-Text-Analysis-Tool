package com.alexandria.controller;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {

    @Mock
    private TextDAO textDAO;

    private final UserSessionController session = UserSessionController.getInstance();

    private ProjectController controller;

    @Before
    public void setUp() {
        session.logout();
        controller = newControllerWithoutView();
    }

    @After
    public void cleanUp() {
        session.logout();
    }

    @Test
    public void createPasteProjectAsGuestReturnsInMemoryText() {

        NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                "Test Project",
                NewProjectModal.SourceType.PASTE,
                "test.txt",
                "Hello world",
                null,
                NewProjectModal.Destination.ANALYSE);

        ProjectController.Result result = controller.createProject(project);

        assertTrue(result.success());
        assertNotNull(result.text());

        assertNull(result.text().getId());
        assertEquals("Test Project", result.text().getTitle());
        assertEquals("test.txt", result.text().getFileName());
        assertEquals(FileType.MANUAL, result.text().getFileType());
        assertEquals("Hello world", result.text().getContent());

        verifyNoInteractions(textDAO);
    }

    @Test
    public void createTxtProjectAsGuestReadsFileContent()
            throws Exception {

        File file = File.createTempFile("test", ".txt");

        try {
            Files.writeString(
                    file.toPath(),
                    "Hello from file");

            NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                    "File Project",
                    NewProjectModal.SourceType.UPLOAD,
                    "",
                    null,
                    file,
                    NewProjectModal.Destination.ANALYSE);

            ProjectController.Result result = controller.createProject(project);

            assertTrue(result.success());
            assertNotNull(result.text());

            assertEquals(
                    "Hello from file",
                    result.text().getContent());

            assertEquals(
                    FileType.TXT,
                    result.text().getFileType());

            assertEquals(
                    file.getName(),
                    result.text().getFileName());

            verifyNoInteractions(textDAO);

        } finally {
            file.delete();
        }
    }

    @Test
    public void createTxtProjectUsesProvidedFileName()
            throws Exception {

        File file = File.createTempFile("actual", ".txt");

        try {
            Files.writeString(
                    file.toPath(),
                    "Hello");

            NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                    "My Project",
                    NewProjectModal.SourceType.UPLOAD,
                    "custom-name.txt",
                    null,
                    file,
                    NewProjectModal.Destination.ANALYSE);

            ProjectController.Result result = controller.createProject(project);

            assertTrue(result.success());
            assertEquals(
                    "custom-name.txt",
                    result.text().getFileName());

        } finally {
            file.delete();
        }
    }

    @Test
    public void createProjectFailsWhenUploadFileIsMissing() {

        NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                "Test Project",
                NewProjectModal.SourceType.UPLOAD,
                null,
                null,
                null,
                NewProjectModal.Destination.ANALYSE);

        ProjectController.Result result = controller.createProject(project);

        assertFalse(result.success());
        assertNotNull(result.message());
        assertNull(result.text());

        verifyNoInteractions(textDAO);
    }

    @Test
    public void createProjectFailsForUnsupportedFileType()
            throws Exception {

        File file = File.createTempFile("test", ".docx");

        try {
            NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                    "Test Project",
                    NewProjectModal.SourceType.UPLOAD,
                    "",
                    null,
                    file,
                    NewProjectModal.Destination.ANALYSE);

            ProjectController.Result result = controller.createProject(project);

            assertFalse(result.success());
            assertNotNull(result.message());
            assertTrue(
                    result.message()
                            .contains("Unsupported file type"));

            verifyNoInteractions(textDAO);

        } finally {
            file.delete();
        }
    }

    @Test
    public void createProjectAsLoggedInUserPersistsText()
            throws Exception {

        User user = new User(
                1,
                "Lua",
                "lua@example.com",
                null,
                null,
                "hash");

        session.login(user);

        Text persisted = new Text(
                10,
                "My Project",
                "test.txt",
                FileType.MANUAL,
                "Hello world");

        when(textDAO.create(any(Text.class)))
                .thenReturn(persisted);

        NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                "My Project",
                NewProjectModal.SourceType.PASTE,
                "test.txt",
                "Hello world",
                null,
                NewProjectModal.Destination.ANALYSE);

        ProjectController.Result result = controller.createProject(project);

        assertTrue(result.success());
        assertSame(persisted, result.text());

        verify(textDAO).create(any(Text.class));
    }

    @Test
    public void createProjectAsLoggedInUserUsesCurrentUserId()
            throws Exception {

        User user = new User(
                42,
                "Lua",
                "lua@example.com",
                null,
                null,
                "hash");

        session.login(user);

        Text persisted = new Text(
                10,
                "My Project",
                "test.txt",
                FileType.MANUAL,
                "Hello");

        when(textDAO.create(any(Text.class)))
                .thenReturn(persisted);

        NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                "My Project",
                NewProjectModal.SourceType.PASTE,
                "test.txt",
                "Hello",
                null,
                NewProjectModal.Destination.ANALYSE);

        ProjectController.Result result = controller.createProject(project);

        assertTrue(result.success());

        verify(textDAO).create(
                org.mockito.ArgumentMatchers.argThat(
                        text -> text.getUserId() == 42
                                && text.getTitle().equals("My Project")
                                && text.getFileName().equals("test.txt")
                                && text.getFileType() == FileType.MANUAL
                                && text.getContent().equals("Hello")));
    }

    @Test
    public void createProjectReturnsErrorWhenDatabaseFails()
            throws Exception {

        User user = new User(
                1,
                "Lua",
                "lua@example.com",
                null,
                null,
                "hash");

        session.login(user);

        when(textDAO.create(any(Text.class)))
                .thenThrow(new RuntimeException("Database unavailable"));

        NewProjectModal.CreatedProject project = new NewProjectModal.CreatedProject(
                "My Project",
                NewProjectModal.SourceType.PASTE,
                "test.txt",
                "Hello",
                null,
                NewProjectModal.Destination.ANALYSE);

        ProjectController.Result result = controller.createProject(project);

        assertFalse(result.success());
        assertNotNull(result.message());
        assertTrue(
                result.message()
                        .contains("Database unavailable"));

        assertNull(result.text());
    }

    private ProjectController newControllerWithoutView() {
        return new ProjectController(textDAO);
    }

}