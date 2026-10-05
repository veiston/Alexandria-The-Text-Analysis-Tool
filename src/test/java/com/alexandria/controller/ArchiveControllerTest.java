package com.alexandria.controller;

import com.alexandria.dao.TermComparisonDAO;
import com.alexandria.dao.TermComparisonTextDAO;
import com.alexandria.dao.TextComparisonDAO;
import com.alexandria.dao.TextComparisonTextDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.model.TermComparison;
import com.alexandria.model.TermComparisonText;
import com.alexandria.model.TextComparison;
import com.alexandria.model.TextComparisonText;
import com.alexandria.model.User;
import com.alexandria.service.ArchiveTermAnalysisService;
import com.alexandria.service.ArchiveTextAnalysisService;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.screens.ArchiveScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveControllerTest {

    @Mock
    private ArchiveTextAnalysisService archiveTextAnalysisService;

    @Mock
    private ArchiveTermAnalysisService archiveTermAnalysisService;

    @Mock
    private TextComparisonDAO textComparisonDAO;

    @Mock
    private TextComparisonTextDAO textComparisonTextDAO;

    @Mock
    private TermComparisonDAO termComparisonDAO;

    @Mock
    private TermComparisonTextDAO termComparisonTextDAO;

    @Mock
    private TextDAO textDAO;

    @Mock
    private ArchiveScreen archiveScreen;

    private final UserSessionController session = UserSessionController.getInstance();

    @Before
    public void setUp() {
        // The session is a singleton and every ArchiveController registers a listener
        // on it.
        // Listeners left over from other tests would react to this test's login/logout.
        session.clearListeners();
        session.logout();
    }

    @After
    public void cleanUp() {
        session.clearListeners();
        session.logout();
    }

    // -----------------------------------------------------------------
    // loadAnalyses()
    // -----------------------------------------------------------------

    @Test
    public void loadsAnalysesForLoggedInUser() throws SQLException {
        List<ArchiveTextAnalysis> textAnalyses = List.of();
        List<ArchiveTermAnalysis> termAnalyses = List.of();

        when(archiveTextAnalysisService.findAllByUserId(1))
                .thenReturn(textAnalyses);

        when(archiveTermAnalysisService.findAllByUserId(1))
                .thenReturn(termAnalyses);

        // Log in first: the controller registers a session listener, so logging in
        // afterwards would trigger an extra load.
        session.login(user());

        ArchiveController controller = newController();

        controller.loadAnalyses();

        verify(archiveTextAnalysisService)
                .findAllByUserId(1);

        verify(archiveTermAnalysisService)
                .findAllByUserId(1);

        verify(textComparisonDAO)
                .findAllByUserId(1);

        verify(termComparisonDAO)
                .findAllByUserId(1);

        verify(archiveScreen)
                .setTextAnalyses(textAnalyses);

        verify(archiveScreen)
                .setTermAnalyses(termAnalyses);

        verify(archiveScreen)
                .setTextComparisons(List.of());

        verify(archiveScreen)
                .setTermComparisons(List.of());
    }

    @Test
    public void loadsAnalysesWhenUserLogsIn() throws SQLException {
        newController();

        session.login(user());

        verify(archiveTextAnalysisService, atLeastOnce())
                .findAllByUserId(1);

        verify(archiveTermAnalysisService, atLeastOnce())
                .findAllByUserId(1);

        verify(archiveScreen, atLeastOnce())
                .setTextComparisons(List.of());
    }

    @Test
    public void showsSignInMessageWithoutUser() {
        ArchiveController controller = newController();

        controller.loadAnalyses();

        verify(archiveScreen)
                .showSignInMessage();

        verifyNoInteractions(archiveTextAnalysisService);
        verifyNoInteractions(archiveTermAnalysisService);
        verifyNoInteractions(textComparisonDAO);
        verifyNoInteractions(termComparisonDAO);
    }

    @Test
    public void skipsUnreadableTextComparisonsInsteadOfFailingTheWholeArchive()
            throws SQLException {

        when(textComparisonDAO.findAllByUserId(1))
                .thenReturn(List.of(new TextComparison(7, 1, "not valid json", null)));

        session.login(user());

        ArchiveController controller = newController();

        controller.loadAnalyses();

        // The bad row is dropped, the rest of the archive still loads.
        verify(archiveScreen)
                .setTextComparisons(List.of());

        verify(archiveScreen)
                .setTextAnalyses(List.of());

        verifyNoInteractions(textDAO);
    }

    @Test
    public void skipsUnreadableTermComparisonsInsteadOfFailingTheWholeArchive()
            throws SQLException {

        when(termComparisonDAO.findAllByUserId(1))
                .thenReturn(List.of(new TermComparison(7, 1, "term", "not valid json", null)));

        session.login(user());

        ArchiveController controller = newController();

        controller.loadAnalyses();

        verify(archiveScreen)
                .setTermComparisons(List.of());

        verify(archiveScreen)
                .setTermAnalyses(List.of());

        verifyNoInteractions(textDAO);
    }

    // -----------------------------------------------------------------
    // deleteTextAnalysis()
    // -----------------------------------------------------------------

    @Test
    public void deletesTextAnalysisForLoggedInUser()
            throws SQLException {

        session.login(user());

        ArchiveController controller = newController();

        controller.deleteTextAnalysis(2);

        verify(archiveTextAnalysisService)
                .deleteById(2, 1);
    }

    @Test
    public void deleteTextAnalysisDoesNothingWithoutUser()
            throws SQLException {

        ArchiveController controller = newController();

        controller.deleteTextAnalysis(2);

        verifyNoInteractions(archiveTextAnalysisService);
    }

    // -----------------------------------------------------------------
    // deleteTermAnalysis()
    // -----------------------------------------------------------------

    @Test
    public void deletesTermAnalysisForLoggedInUser()
            throws SQLException {

        session.login(user());

        ArchiveController controller = newController();

        controller.deleteTermAnalysis(3);

        verify(archiveTermAnalysisService)
                .deleteById(3, 1);
    }

    @Test
    public void deleteTermAnalysisDoesNothingWithoutUser()
            throws SQLException {

        ArchiveController controller = newController();

        controller.deleteTermAnalysis(3);

        verifyNoInteractions(archiveTermAnalysisService);
    }

    // -----------------------------------------------------------------
    // deleteTextComparison()
    // -----------------------------------------------------------------

    @Test
    public void deletesTextComparisonAndItsTextLinks()
            throws SQLException {

        when(textComparisonDAO.findById(7))
                .thenReturn(new TextComparison(7, 1, "{}", null));

        when(textComparisonTextDAO.findAllByComparisonId(7))
                .thenReturn(List.of(
                        new TextComparisonText(7, 10),
                        new TextComparisonText(7, 11)));

        session.login(user());

        ArchiveController controller = newController();

        controller.deleteTextComparison(7);

        verify(textComparisonTextDAO)
                .delete(7, 10);

        verify(textComparisonTextDAO)
                .delete(7, 11);

        verify(textComparisonDAO)
                .delete(7);
    }

    @Test
    public void deleteTextComparisonRejectsAnotherUsersComparison()
            throws SQLException {

        when(textComparisonDAO.findById(7))
                .thenReturn(new TextComparison(7, 99, "{}", null));

        session.login(user());

        ArchiveController controller = newController();

        // ErrorAlert needs the JavaFX thread, so it is mocked out here.
        try (MockedStatic<ErrorAlert> errorAlert = mockStatic(ErrorAlert.class)) {
            controller.deleteTextComparison(7);

            errorAlert.verify(() -> ErrorAlert.show("Could not delete text comparison."));
        }

        verify(textComparisonDAO, never())
                .delete(anyInt());

        verify(textComparisonTextDAO, never())
                .delete(anyInt(), anyInt());
    }

    @Test
    public void deleteTextComparisonDoesNothingWithoutUser()
            throws SQLException {

        ArchiveController controller = newController();

        controller.deleteTextComparison(7);

        verifyNoInteractions(textComparisonDAO);
        verifyNoInteractions(textComparisonTextDAO);
    }

    // -----------------------------------------------------------------
    // deleteTermComparison()
    // -----------------------------------------------------------------

    @Test
    public void deletesTermComparisonAndItsTextLinks()
            throws SQLException {

        when(termComparisonDAO.findById(8))
                .thenReturn(new TermComparison(8, 1, "term", "{}", null));

        when(termComparisonTextDAO.findAllByComparisonId(8))
                .thenReturn(List.of(
                        new TermComparisonText(8, 10),
                        new TermComparisonText(8, 11)));

        session.login(user());

        ArchiveController controller = newController();

        controller.deleteTermComparison(8);

        verify(termComparisonTextDAO)
                .delete(8, 10);

        verify(termComparisonTextDAO)
                .delete(8, 11);

        verify(termComparisonDAO)
                .delete(8);
    }

    @Test
    public void deleteTermComparisonRejectsAnotherUsersComparison()
            throws SQLException {

        when(termComparisonDAO.findById(8))
                .thenReturn(new TermComparison(8, 99, "term", "{}", null));

        session.login(user());

        ArchiveController controller = newController();

        try (MockedStatic<ErrorAlert> errorAlert = mockStatic(ErrorAlert.class)) {
            controller.deleteTermComparison(8);

            errorAlert.verify(() -> ErrorAlert.show("Could not delete term comparison."));
        }

        verify(termComparisonDAO, never())
                .delete(anyInt());

        verify(termComparisonTextDAO, never())
                .delete(anyInt(), anyInt());
    }

    @Test
    public void deleteTermComparisonDoesNothingWithoutUser()
            throws SQLException {

        ArchiveController controller = newController();

        controller.deleteTermComparison(8);

        verifyNoInteractions(termComparisonDAO);
        verifyNoInteractions(termComparisonTextDAO);
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    private ArchiveController newController() {
        return new ArchiveController(
                archiveTextAnalysisService,
                archiveTermAnalysisService,
                textComparisonDAO,
                textComparisonTextDAO,
                termComparisonDAO,
                termComparisonTextDAO,
                textDAO,
                archiveScreen);
    }

    private User user() {
        return new User(
                1,
                "Test user",
                "test@example.com",
                null,
                null,
                "password");
    }

}