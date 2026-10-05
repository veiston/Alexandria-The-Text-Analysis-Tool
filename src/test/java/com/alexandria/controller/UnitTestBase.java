package com.alexandria.controller;

import com.alexandria.utils.SessionStorage;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for Alexandria unit tests.
 *
 * Unit tests:
 * - do not start JavaFX
 * - do not start TestFX
 * - do not connect to the database
 * - do not use the real application
 * - use Mockito for external dependencies
 *
 * This base class only resets application-level session state so
 * tests remain independent when they use UserSessionController.
 */
public abstract class UnitTestBase {

    protected final UserSessionController session = UserSessionController.getInstance();

    protected final SessionStorage sessionStorage = new SessionStorage();

    /**
     * Runs before every unit test.
     *
     * The UserSessionController is a singleton, so its state must be
     * explicitly reset before each test.
     */
    @Before
    public void setUpUnitTest() {
        resetSession();
    }

    /**
     * Runs after every unit test.
     *
     * This protects the next test from state created by the current test.
     */
    @After
    public void tearDownUnitTest() {
        resetSession();
    }

    /**
     * Resets both in-memory and persisted session state.
     *
     * Controllers register listeners on the singleton session and never remove
     * them,
     * so listeners are cleared too. Otherwise a controller created in one test
     * would
     * react to the login/logout of every later test.
     */
    protected void resetSession() {
        session.clearListeners();
        session.logout();
        sessionStorage.clear();
    }
}