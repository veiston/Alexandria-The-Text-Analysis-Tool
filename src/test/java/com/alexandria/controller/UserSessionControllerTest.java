package com.alexandria.controller;

import com.alexandria.model.User;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

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
}
