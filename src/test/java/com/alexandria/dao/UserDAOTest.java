package com.alexandria.dao;

import com.alexandria.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserDAOTest {
	private User user;
    private UserDAO userDAO;

    @Before
    public void setup() throws SQLException {
        user = new User(
                "Test user",
                "test" + UUID.randomUUID() + "@test.com",
                "/upload/test_photo.png",
                "Test organization",
                "test-hashed-password"
        );

		userDAO = new UserDAO();
		user = userDAO.create(user);
    }

    @After
    public void cleanup() throws SQLException {
        if (user.getId() != null) {
            userDAO.delete(user.getId());
        }
    }

    @Test
    public void createsUser() {
        assertNotNull(user.getId());
    }

    @Test
    public void findsUserById() throws SQLException {
        User foundUser = userDAO.findById(user.getId());

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    public void findsUserByEmail() throws SQLException {
        User foundUser = userDAO.findByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    public void updatesUser() throws SQLException {
        user.setName("Updated test user");
		user.setEmail("updated-" + UUID.randomUUID() + "@test.com");
		user.setPhoto("/upload/updated_test_photo.png");
		user.setOrganization("Updated test organization");
		user.setPassword("updated-hashed-password");

		boolean updatedUser = userDAO.update(user);

		assertTrue(updatedUser);
    }

    @Test
    public void deletesUser() throws SQLException {
        assertTrue(userDAO.delete(user.getId()));
        assertNull(userDAO.findById(user.getId()));
    }
}
