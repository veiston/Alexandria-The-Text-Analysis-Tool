package com.alexandria.controller;

import com.alexandria.dao.TextDAO;
import com.alexandria.dao.UserDAO;
import com.alexandria.service.PdfService;
import com.alexandria.view.MainView;
import com.alexandria.view.screens.ArchiveScreen;
import com.alexandria.view.screens.LibraryScreen;
import com.alexandria.view.screens.ProfileScreen;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MainControllerWiringTest {

    @Test
    public void constructor_restoresSessionAndConstructsEveryCollaborator() throws Exception {
        UserSessionController session = mock(UserSessionController.class);

        try (MockedStatic<UserSessionController> sessionStatic = mockStatic(UserSessionController.class);
                MockedConstruction<UserDAO> userDaoConstruction = mockConstruction(UserDAO.class);
                MockedConstruction<MainView> mainViewConstruction = mockConstruction(MainView.class);
                MockedConstruction<ProfileScreen> profileScreenConstruction = mockConstruction(ProfileScreen.class);
                MockedConstruction<ArchiveScreen> archiveScreenConstruction = mockConstruction(ArchiveScreen.class);
                MockedConstruction<LibraryScreen> libraryScreenConstruction = mockConstruction(LibraryScreen.class);
                MockedConstruction<ProfileController> profileControllerConstruction = mockConstruction(
                        ProfileController.class);
                MockedConstruction<ArchiveController> archiveControllerConstruction = mockConstruction(
                        ArchiveController.class);
                MockedConstruction<TextDAO> textDaoConstruction = mockConstruction(TextDAO.class);
                MockedConstruction<PdfService> pdfServiceConstruction = mockConstruction(PdfService.class);
                MockedConstruction<ProjectController> projectControllerConstruction = mockConstruction(
                        ProjectController.class);
                MockedConstruction<AnalyseController> analyseControllerConstruction = mockConstruction(
                        AnalyseController.class)) {

            sessionStatic.when(UserSessionController::getInstance).thenReturn(session);

            MainController controller = new MainController();

            assertNotNull(controller);
            assertNotNull(controller.getView());

            // restoreSession()
            assertEquals(1, userDaoConstruction.constructed().size());
            verify(session).restore(userDaoConstruction.constructed().get(0));

            // configureProfile(): Route.PROFILE.createScreen() -> new ProfileScreen(),
            // then new ProfileController(userDAO, profileScreen)
            assertEquals(1, profileScreenConstruction.constructed().size());
            assertEquals(1, profileControllerConstruction.constructed().size());

            // configureArchive(): Route.ARCHIVE.createScreen() -> new ArchiveScreen(),
            // then new ArchiveController(archiveScreen)
            assertEquals(1, archiveScreenConstruction.constructed().size());
            assertEquals(1, archiveControllerConstruction.constructed().size());

            // configureLibrary() and configureProject() each construct a TextDAO.
            assertEquals(2, textDaoConstruction.constructed().size());
            assertEquals(2, pdfServiceConstruction.constructed().size());
            assertEquals(1, projectControllerConstruction.constructed().size());

            // AnalyseController built directly in the constructor
            assertEquals(1, analyseControllerConstruction.constructed().size());

            MainView mainView = mainViewConstruction.constructed().get(0);

            // configureUserGuideTour(): OPEN_ANALYSIS_EVENT, CLOSE_ANALYSIS_EVENT,
            // CLOSE_ARCHIVE_EVENT
            verify(mainView, times(3)).addEventHandler(any(), any());

            // configureProject(): wires the project-created callback
            verify(mainView).setOnProjectCreated(any());
        }
    }

    @Test
    public void constructor_sessionRestoreThrows_doesNotPropagate() throws Exception {
        UserSessionController session = mock(UserSessionController.class);
        doThrow(new RuntimeException("db unreachable")).when(session).restore(any());

        try (MockedStatic<UserSessionController> sessionStatic = mockStatic(UserSessionController.class);
                MockedConstruction<UserDAO> userDaoConstruction = mockConstruction(UserDAO.class);
                MockedConstruction<MainView> mainViewConstruction = mockConstruction(MainView.class);
                MockedConstruction<ProfileScreen> profileScreenConstruction = mockConstruction(ProfileScreen.class);
                MockedConstruction<ArchiveScreen> archiveScreenConstruction = mockConstruction(ArchiveScreen.class);
                MockedConstruction<LibraryScreen> libraryScreenConstruction = mockConstruction(LibraryScreen.class);
                MockedConstruction<ProfileController> profileControllerConstruction = mockConstruction(
                        ProfileController.class);
                MockedConstruction<ArchiveController> archiveControllerConstruction = mockConstruction(
                        ArchiveController.class);
                MockedConstruction<TextDAO> textDaoConstruction = mockConstruction(TextDAO.class);
                MockedConstruction<PdfService> pdfServiceConstruction = mockConstruction(PdfService.class);
                MockedConstruction<ProjectController> projectControllerConstruction = mockConstruction(
                        ProjectController.class);
                MockedConstruction<AnalyseController> analyseControllerConstruction = mockConstruction(
                        AnalyseController.class)) {

            sessionStatic.when(UserSessionController::getInstance).thenReturn(session);

            MainController controller = new MainController(); // must not throw

            assertNotNull(controller);
        }
    }
}