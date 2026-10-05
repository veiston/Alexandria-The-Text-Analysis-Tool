package com.alexandria.controller;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.service.FileStorageService;
import com.alexandria.view.screens.LibraryScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerTest extends JavaFxTestBase {

    @Mock
    private TextDAO textDAO;

    @Mock
    private LibraryScreen libraryScreen;

    @Mock
    private FileStorageService fileStorageService;

    private final UserSessionController session = UserSessionController.getInstance();

    @Before
    public void setUp() throws Exception {
        runOnFxThread(session::logout);
    }

    @After
    public void cleanUp() throws Exception {
        runOnFxThread(session::logout);
    }

    @Test
    public void loadsTextsAfterLogin() throws Exception {
        runOnFxThread(() -> {
            List<Text> texts = List.of(
                    sampleText(1, "Meditations"));

            try {
                when(textDAO.findAllByUserId(1))
                        .thenReturn(texts);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            newController();

            session.login(user());

            try {
                verify(textDAO)
                        .findAllByUserId(1);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            verify(libraryScreen)
                    .setTexts(texts);
        });
    }

    @Test
    public void showsSignInMessageWithoutUser() throws Exception {
        runOnFxThread(() -> {
            LibraryController controller = newController();

            controller.loadTexts();

            verify(libraryScreen)
                    .showSignInMessage();

            verifyNoInteractions(textDAO);
        });
    }

    @Test
    public void deletesTextWhenUserIsLoggedIn() throws Exception {
        runOnFxThread(() -> {
            LibraryController controller = newController();

            session.login(user());

            controller.deleteText(5);

            try {
                verify(textDAO)
                        .delete(5);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    @Test
    public void deleteTextIgnoredWhenNoUserIsLoggedIn() throws Exception {
        runOnFxThread(() -> {
            LibraryController controller = newController();

            controller.deleteText(5);

            try {
                verify(textDAO, never())
                        .delete(5);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            verifyNoInteractions(textDAO);
        });
    }

    private LibraryController newController() {
        return new LibraryController(
                textDAO,
                libraryScreen,
                fileStorageService,
                (text, file) -> {
                },
                (texts, files) -> {
                },
                (text, file) -> {
                },
                () -> {
                });
    }

    private User user() {
        return new User(
                1,
                "Test user",
                "test@example.com",
                null,
                null,
                "password");
    }

    private Text sampleText(int id, String title) {
        return new Text(
                id,
                1,
                title,
                "meditations.pdf",
                null,
                FileType.PDF,
                "content",
                LocalDateTime.now());
    }

}