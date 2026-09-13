package com.alexandria.controller;

import com.alexandria.model.User;
import com.alexandria.dao.UserDAO;
import com.alexandria.utils.SessionStorage;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class UserSessionControllerTest {

    private final UserSessionController session = UserSessionController.getInstance();

    @After
    public void cleanUp() {
        session.logout(); // reset shared singleton state after each test
    }

    @Test
    public void startsLoggedOut() {
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }

    @Test
    public void loginSetsCurrentUser() {
        User user = new User(1, "Lua", "lua@example.com", null, null, "hash");
        session.login(user);

        assertTrue(session.isLoggedIn());
        assertEquals("Lua", session.getCurrentUser().getName());
    }

    @Test
    public void logoutClearsCurrentUser() {
        session.login(new User(1, "Lua", "lua@example.com", null, null, "hash"));
        session.logout();

        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }

    @Test
    public void listenerFiresOnLoginAndLogout() {
        ArrayList<User> receivedUsers = new ArrayList<>();
        session.addListener(receivedUsers::add);

        User user = new User(1, "Lua", "lua@example.com", null, null, "hash");
        session.login(user);
        session.logout();

        assertEquals(2, receivedUsers.size());
        assertNotNull(receivedUsers.get(0));
        assertNull(receivedUsers.get(1));
    }

    @Test
    public void restoreDoesNothingWhenNoUserIdIsStored() throws SQLException {
        UserDAO userDAO = mock(UserDAO.class);

        session.restore(userDAO);

        assertFalse(session.isLoggedIn());
        verifyNoInteractions(userDAO);
    }

    @Test
    public void restoreLogsInStoredUser() throws SQLException {
        UserDAO userDAO = mock(UserDAO.class);
        User user = new User(1, "Existing", "taken@example.com", null, null, "hash");
        new SessionStorage().saveUserId(1);
        when(userDAO.findById(1)).thenReturn(user);

        session.restore(userDAO);

        assertTrue(session.isLoggedIn());
        assertEquals(user, session.getCurrentUser());
    }

    @Test
    public void restoreClearsStoredIdWhenUserIsMissing() throws SQLException {
        UserDAO userDAO = mock(UserDAO.class);
        new SessionStorage().saveUserId(1);
        when(userDAO.findById(1)).thenReturn(null);

        session.restore(userDAO);

        assertFalse(session.isLoggedIn());
        assertNull(new SessionStorage().getUserId());
    }

    @Test
    public void restoreDoesNothingWhenUserIsAlreadyLoggedIn() throws SQLException {
        UserDAO userDAO = mock(UserDAO.class);
        session.login(new User(1, "Existing", "taken@example.com", null, null, "hash"));

        session.restore(userDAO);

        assertTrue(session.isLoggedIn());
        verifyNoInteractions(userDAO);
    }
}
