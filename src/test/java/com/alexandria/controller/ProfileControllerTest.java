package com.alexandria.controller;

import com.alexandria.dao.UserDAO;
import com.alexandria.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileControllerTest {

        @Mock
        private UserDAO userDAO;

        private final UserSessionController session = UserSessionController.getInstance();

        private ProfileController controller;

        @Before
        public void setUp() {
                session.logout();
        }

        @After
        public void cleanUp() {
                session.logout();
        }

        @Test
        public void registerCreatesUserAndLogsIn() throws SQLException {
                when(userDAO.findByEmail("new@example.com"))
                                .thenReturn(null);

                doAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        user.setId(1);
                        return null;
                }).when(userDAO).create(any(User.class));

                controller = newControllerWithoutScreen();

                Map<String, String> values = Map.of(
                                "name", "Lua",
                                "email", "new@example.com",
                                "password", "password123",
                                "organization", "",
                                "photo", "");

                ProfileController.Result result = controller.register(values);

                assertTrue(result.success());
                verify(userDAO).create(any(User.class));
                assertTrue(session.isLoggedIn());

        }

        @Test
        public void registerFailsIfEmailAlreadyExists() throws SQLException {
                User existing = new User(
                                1,
                                "Existing",
                                "taken@example.com",
                                null,
                                null,
                                "hash");

                when(userDAO.findByEmail("taken@example.com"))
                                .thenReturn(existing);

                controller = newControllerWithoutScreen();

                Map<String, String> values = Map.of(
                                "name", "Lua",
                                "email", "taken@example.com",
                                "password", "password123",
                                "organization", "",
                                "photo", "");

                ProfileController.Result result = controller.register(values);

                assertFalse(result.success());
                verify(userDAO, never()).create(any(User.class));
                assertFalse(session.isLoggedIn());
        }

        @Test
        public void loginSucceedsWithCorrectPassword() throws SQLException {
                User stored = new User(
                                1,
                                "Lua",
                                "lua@example.com",
                                null,
                                null,
                                com.alexandria.utils.PasswordHasher.hash(
                                                "correctPassword"));

                when(userDAO.findByEmail("lua@example.com"))
                                .thenReturn(stored);

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.login(
                                "lua@example.com",
                                "correctPassword");

                assertTrue(result.success());
                assertTrue(session.isLoggedIn());
        }

        @Test
        public void loginFailsWithWrongPassword() throws SQLException {
                User stored = new User(
                                1,
                                "Lua",
                                "lua@example.com",
                                null,
                                null,
                                com.alexandria.utils.PasswordHasher.hash(
                                                "correctPassword"));

                when(userDAO.findByEmail("lua@example.com"))
                                .thenReturn(stored);

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.login(
                                "lua@example.com",
                                "wrongPassword");

                assertFalse(result.success());
                assertFalse(session.isLoggedIn());
        }

        @Test
        public void loginFailsIfUserNotFound() throws SQLException {
                when(userDAO.findByEmail("nobody@example.com"))
                                .thenReturn(null);

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.login(
                                "nobody@example.com",
                                "anyPassword");

                assertFalse(result.success());
                assertFalse(session.isLoggedIn());
        }

        @Test
        public void editProfileFailsWhenNotLoggedIn() {
                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.editProfile(
                                Map.of("name", "New Name"));

                assertFalse(result.success());
                assertEquals(
                                "You must be logged in.",
                                result.message());
        }

        @Test
        public void editProfileUpdatesOnlyProvidedFields()
                        throws SQLException {

                User user = new User(
                                1,
                                "OldName",
                                "old@example.com",
                                null,
                                "OldOrg",
                                "hash");

                session.login(user);

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.editProfile(Map.of(
                                "name", "NewName",
                                "email", "",
                                "organization", "",
                                "photo", ""));

                assertTrue(result.success());
                assertEquals("NewName", user.getName());

                // Blank email should be ignored.
                assertEquals(
                                "old@example.com",
                                user.getEmail());

                verify(userDAO).update(user);
        }

        @Test
        public void editProfileUpdatesProvidedTextFields() throws SQLException {
                User user = new User(
                                1,
                                "OldName",
                                "old@example.com",
                                null,
                                "OldOrg",
                                "hash");
                session.login(user);

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.editProfile(Map.of(
                                "name", "NewName",
                                "email", "new@example.com",
                                "organization", "OldOrg",
                                "photo", ""));

                assertTrue(result.success());
                assertEquals("NewName", user.getName());
                assertEquals("new@example.com", user.getEmail());
                assertEquals("OldOrg", user.getOrganization());
        }

        @Test
        public void registerReturnsErrorWhenDaoFails() throws SQLException {
                when(userDAO.findByEmail("new@example.com"))
                                .thenThrow(new SQLException("Database unavailable"));

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.register(Map.of(
                                "name", "Lua",
                                "email", "new@example.com",
                                "password", "password123",
                                "organization", "",
                                "photo", ""));

                assertFalse(result.success());
                assertTrue(result.message().contains("Registration failed"));
        }

        @Test
        public void loginReturnsErrorWhenDaoFails() throws SQLException {
                when(userDAO.findByEmail("lua@example.com"))
                                .thenThrow(new SQLException("Database unavailable"));

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.login(
                                "lua@example.com",
                                "correctPassword");

                assertFalse(result.success());
                assertTrue(result.message().contains("Login failed"));
        }

        @Test
        public void changePasswordUpdatesLoggedInUser() throws SQLException {
                User user = new User(
                                1,
                                "Lua",
                                "lua@example.com",
                                null,
                                null,
                                com.alexandria.utils.PasswordHasher.hash("correctPassword"));
                session.login(user);

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.changePassword("wrongPassword");

                assertTrue(result.success());
                assertTrue(com.alexandria.utils.PasswordHasher.matches(
                                "wrongPassword",
                                user.getPassword()));
                verify(userDAO).update(user);
        }

        @Test
        public void changePasswordFailsWhenNoUserIsLoggedIn() {
                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.changePassword("wrongPassword");

                assertFalse(result.success());
        }

        @Test
        public void changePasswordReturnsErrorWhenDaoFails() throws SQLException {
                User user = new User(
                                1,
                                "Lua",
                                "taken@example.com",
                                null,
                                null,
                                "hash");
                session.login(user);
                when(userDAO.update(user))
                                .thenThrow(new SQLException("Database unavailable"));

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.changePassword("wrongPassword");

                assertFalse(result.success());
                assertTrue(result.message().contains("Password change failed"));
        }

        @Test
        public void editProfileReturnsErrorWhenDaoFails() throws SQLException {
                User user = new User(
                                1,
                                "Lua",
                                "lua@example.com",
                                null,
                                null,
                                "hash");
                session.login(user);
                when(userDAO.update(any(User.class)))
                                .thenThrow(new SQLException("Database unavailable"));

                controller = newControllerWithoutScreen();

                ProfileController.Result result = controller.editProfile(
                                Map.of("name", "NewName"));

                assertFalse(result.success());
                assertTrue(result.message().contains("Update failed"));
        }

        private ProfileController newControllerWithoutScreen() {
                return new ProfileController(userDAO);
        }

}
