package com.alexandria.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.view.screens.LibraryScreen;

import javafx.application.Platform;

@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Mock
    private TextDAO textDAO;

    @Mock
    private LibraryScreen libraryScreen;

    private final UserSessionController session = UserSessionController.getInstance();

    @Before
    @After
    public void resetSession() {
        session.logout();
    }

    @Test
    public void loadsTextsAfterLogin() throws SQLException {
        List<Text> texts = List.of(sampleText(1, "Meditations"));
        when(textDAO.findAllByUserId(1)).thenReturn(texts);

        newController();
        session.login(user());

        verify(libraryScreen).setTexts(texts);
    }

    @Test
    public void showsSignInMessageWithoutUser() {
        newController().loadTexts();
        verify(libraryScreen).showSignInMessage();
    }

    @Test
    public void deletesTextWhenUserIsLoggedIn() throws SQLException {
        session.login(user());
        newController().deleteText(5);

        verify(textDAO).delete(5);
    }

    @Test
    public void deleteTextIgnoredWhenNoUserIsLoggedIn() throws SQLException {
        newController().deleteText(5);
        verify(textDAO, never()).delete(5);
    }

    private LibraryController newController() {
        return new LibraryController(textDAO, libraryScreen, text -> {}, text -> {}, () -> {});
    }

    private User user() {
        return new User(1, "Test user", "test@example.com", null, null, "password");
    }

    private Text sampleText(int id, String title) {
        return new Text(id, 1, title, "meditations.pdf", FileType.PDF, "content", LocalDateTime.now());
    }
}
