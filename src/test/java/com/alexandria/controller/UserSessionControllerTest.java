package com.alexandria.controller;

import com.alexandria.dao.UserDAO;
import com.alexandria.model.User;
import com.alexandria.utils.SessionStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserSessionController.
 *
 * These tests use Mockito for UserDAO and do not connect to the
 * database. SessionStorage is cleared before and after each test
 * to prevent persisted session state from leaking between tests.
 */
public class UserSessionControllerTest {

    private UserSessionController session;
    private SessionStorage storage;

    @Before
    public void setUp() {
        session = UserSessionController.getInstance();
        storage = new SessionStorage();

        session.clearListeners();
        session.logout();
        storage.clear();
    }

    @After
    public void tearDown() {
        session.clearListeners();
        session.logout();
        storage.clear();
    }

    // -----------------------------------------------------------------
    // Initial state
    // -----------------------------------------------------------------

    @Test
    public void startsLoggedOut() {
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }

    // -----------------------------------------------------------------
    // Login
    // -----------------------------------------------------------------

    @Test
    public void loginSetsCurrentUser() {
        User user = createUser(1, "Lua", "lua@example.com");

        session.login(user);

        assertTrue(session.isLoggedIn());
        assertSame(user, session.getCurrentUser());
        assertEquals("Lua", session.getCurrentUser().getName());
    }

    @Test
    public void loginPersistsUserId() {
        User user = createUser(1, "Lua", "lua@example.com");

        session.login(user);

        assertEquals(
                Integer.valueOf(1),
                storage.getUserId());
    }

    @Test
    public void loginNotifiesListeners() {
        ArrayList<User> receivedUsers = new ArrayList<>();

        session.addListener(receivedUsers::add);

        User user = createUser(1, "Lua", "lua@example.com");

        session.login(user);

        assertEquals(1, receivedUsers.size());
        assertSame(user, receivedUsers.get(0));
    }

    // -----------------------------------------------------------------
    // Logout
    // -----------------------------------------------------------------

    @Test
    public void logoutClearsCurrentUser() {
        User user = createUser(1, "Lua", "lua@example.com");

        session.login(user);
        session.logout();

        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }

    @Test
    public void logoutClearsStoredUserId() {
        User user = createUser(1, "Lua", "lua@example.com");

        session.login(user);
        assertNotNull(storage.getUserId());

        session.logout();

        assertNull(storage.getUserId());
    }

    @Test
    public void logoutNotifiesListenersWithNull() {
        ArrayList<User> receivedUsers = new ArrayList<>();

        session.addListener(receivedUsers::add);

        User user = createUser(1, "Lua", "lua@example.com");

        session.login(user);
        session.logout();

        assertEquals(2, receivedUsers.size());
        assertSame(user, receivedUsers.get(0));
        assertNull(receivedUsers.get(1));
    }

    // -----------------------------------------------------------------
    // Restore
    // -----------------------------------------------------------------

    @Test
    public void restoreDoesNothingWhenNoUserIdIsStored()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        session.restore(userDAO);

        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());

        verifyNoInteractions(userDAO);
    }

    @Test
    public void restoreLogsInStoredUser()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        User user = createUser(
                1,
                "Existing",
                "existing@example.com");

        storage.saveUserId(1);

        when(userDAO.findById(1))
                .thenReturn(user);

        session.restore(userDAO);

        assertTrue(session.isLoggedIn());
        assertSame(user, session.getCurrentUser());
        assertEquals(
                "Existing",
                session.getCurrentUser().getName());

        verify(userDAO).findById(1);
    }

    @Test
    public void restoreDoesNotChangeStoredUserIdWhenUserExists()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        User user = createUser(
                1,
                "Existing",
                "existing@example.com");

        storage.saveUserId(1);

        when(userDAO.findById(1))
                .thenReturn(user);

        session.restore(userDAO);

        assertEquals(
                Integer.valueOf(1),
                storage.getUserId());
    }

    @Test
    public void restoreClearsStoredIdWhenUserIsMissing()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        storage.saveUserId(1);

        when(userDAO.findById(1))
                .thenReturn(null);

        session.restore(userDAO);

        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
        assertNull(storage.getUserId());

        verify(userDAO).findById(1);
    }

    @Test
    public void restoreDoesNothingWhenUserIsAlreadyLoggedIn()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        User user = createUser(
                1,
                "Existing",
                "existing@example.com");

        session.login(user);

        session.restore(userDAO);

        assertTrue(session.isLoggedIn());
        assertSame(user, session.getCurrentUser());

        verifyNoInteractions(userDAO);
    }

    @Test
    public void restoreNotifiesListenersWhenUserIsRestored()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        ArrayList<User> receivedUsers = new ArrayList<>();
        session.addListener(receivedUsers::add);

        User user = createUser(
                1,
                "Existing",
                "existing@example.com");

        storage.saveUserId(1);

        when(userDAO.findById(1))
                .thenReturn(user);

        session.restore(userDAO);

        assertEquals(1, receivedUsers.size());
        assertSame(user, receivedUsers.get(0));
    }

    @Test
    public void restoreDoesNotNotifyListenersWhenNoUserIdExists()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        ArrayList<User> receivedUsers = new ArrayList<>();
        session.addListener(receivedUsers::add);

        session.restore(userDAO);

        assertTrue(receivedUsers.isEmpty());

        verifyNoInteractions(userDAO);
    }

    @Test
    public void restoreDoesNotNotifyListenersWhenStoredUserIsMissing()
            throws SQLException {

        UserDAO userDAO = mock(UserDAO.class);

        ArrayList<User> receivedUsers = new ArrayList<>();
        session.addListener(receivedUsers::add);

        storage.saveUserId(1);

        when(userDAO.findById(1))
                .thenReturn(null);

        session.restore(userDAO);

        assertTrue(receivedUsers.isEmpty());
        assertNull(storage.getUserId());
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    private User createUser(
            int id,
            String name,
            String email) {

        return new User(
                id,
                name,
                email,
                null,
                null,
                "hash");
    }
}
