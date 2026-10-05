package com.alexandria.controller;

import javafx.application.Platform;
import org.junit.BeforeClass;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public abstract class JavaFxTestBase {

    // Cold JVM + JaCoCo + Mockito's first self-attach can take several seconds on
    // the first test of a run.
    private static final long FX_TIMEOUT_SECONDS = 20;

    @BeforeClass
    public static void initJavaFx() throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        AtomicReference<Throwable> startupError = new AtomicReference<>();

        try {
            Platform.startup(() -> {
                startupLatch.countDown();
            });
        } catch (IllegalStateException e) {
            // JavaFX was already initialized.
            startupLatch.countDown();
        }

        if (!startupLatch.await(
                FX_TIMEOUT_SECONDS,
                TimeUnit.SECONDS)) {

            fail("Timed out starting JavaFX.");
        }

        Throwable error = startupError.get();

        if (error != null) {
            fail("Failed to start JavaFX: " + error);
        }
    }

    protected void runOnFxThread(Runnable action)
            throws Exception {

        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(
                FX_TIMEOUT_SECONDS,
                TimeUnit.SECONDS)) {

            fail("Timed out waiting for JavaFX thread.");
        }

        if (error.get() != null) {
            throw new RuntimeException(
                    "Exception on JavaFX thread",
                    error.get());
        }
    }
}