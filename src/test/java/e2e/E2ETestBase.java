package e2e;

import com.alexandria.Main;
import com.alexandria.db.DatabaseConnection;
import com.alexandria.utils.SessionStorage;

import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Base class for Alexandria end-to-end tests.
 *
 * E2E tests use:
 * - the real JavaFX application
 * - TestFX
 * - the normal Alexandria database
 * - the real SessionStorage
 *
 * No test database or mocked database is used here.
 */
public abstract class E2ETestBase extends ApplicationTest {

    protected static final String TEST_EMAIL = "test@test.com";
    protected static final String TEST_PASSWORD = "password";

    private final SessionStorage sessionStorage = new SessionStorage();

    /**
     * Runs before TestFX starts the JavaFX application.
     *
     * This must happen before start(), because MainController restores
     * the session in its constructor.
     */
    @Before
    public void prepareTest() throws Exception {
        clearStoredSession();
        verifyDatabaseConnection();
    }

    /**
     * Starts the real Alexandria application.
     */
    @Override
    public void start(Stage stage) throws Exception {
        new Main().start(stage);
    }

    /**
     * Clears the persisted session after each E2E test.
     */
    @After
    public void cleanupTest() {
        sessionStorage.clear();
    }

    /**
     * Clears any persisted user ID.
     *
     * This prevents MainController from automatically restoring a
     * previous login when the application starts.
     */
    protected void clearStoredSession() {
        sessionStorage.clear();
    }

    /**
     * Verifies that the real Alexandria database is reachable.
     *
     * This deliberately uses the application's normal database
     * connection rather than a test-specific database.
     */
    protected void verifyDatabaseConnection() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT 1")) {

            assertTrue(
                    "Could not connect to the Alexandria database.",
                    resultSet.next());

            assertEquals(
                    1,
                    resultSet.getInt(1));
        }
    }

    /**
     * Waits until a JavaFX node matching the selector exists.
     */
    protected void waitForNode(String selector) throws TimeoutException {
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                () -> lookup(selector)
                        .tryQuery()
                        .isPresent());
    }

    /**
     * Waits until a JavaFX node matching the selector exists and is visible.
     */
    protected void waitForVisible(String selector) throws TimeoutException {
        WaitForAsyncUtils.waitFor(
                10,
                TimeUnit.SECONDS,
                () -> lookup(selector)
                        .tryQuery()
                        .map(node -> node.isVisible())
                        .orElse(false));
    }

    /**
     * Logs in through the real Alexandria UI.
     *
     * The test account must already exist in the normal Alexandria
     * database with the password defined above.
     */
    protected void login() {
        clickOn("#loginEmailField");
        write(TEST_EMAIL);

        clickOn("#loginPasswordField");
        write(TEST_PASSWORD);

        clickOn("#loginButton");
    }
}
