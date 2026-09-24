package com.alexandria.controller;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.model.User;
import com.alexandria.service.ArchiveTermAnalysisService;
import com.alexandria.service.ArchiveTextAnalysisService;
import com.alexandria.view.screens.ArchiveScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveControllerTest {

    @Mock
    private ArchiveTextAnalysisService archiveTextAnalysisService;

    @Mock
    private ArchiveTermAnalysisService archiveTermAnalysisService;

    @Mock
    private ArchiveScreen archiveScreen;

    private final UserSessionController session = UserSessionController.getInstance();

    @Before
    public void setUp() {
        session.logout();
    }

    @After
    public void cleanUp() {
        session.logout();
    }

    @Test
    public void loadsAnalysesAfterLogin() throws SQLException {
        List<ArchiveTextAnalysis> textAnalyses = List.of();
        List<ArchiveTermAnalysis> termAnalyses = List.of();

        when(archiveTextAnalysisService.findAllByUserId(1)).thenReturn(textAnalyses);
        when(archiveTermAnalysisService.findAllByUserId(1)).thenReturn(termAnalyses);

        newController();
        session.login(user());

        verify(archiveScreen).setTextAnalyses(textAnalyses);
        verify(archiveScreen).setTermAnalyses(termAnalyses);
    }

    @Test
    public void showsSignInMessageWithoutUser() {
        ArchiveController controller = newController();
        controller.loadAnalyses();

        verify(archiveScreen).showSignInMessage();
    }

    @Test
    public void deletesTextAnalysis() throws SQLException {
        ArchiveController controller = newController();
        session.login(user());
        controller.deleteTextAnalysis(2);

        verify(archiveTextAnalysisService).deleteById(2, 1);
    }

    @Test
    public void deletesTermAnalysis() throws SQLException {
        ArchiveController controller = newController();
        session.login(user());
        controller.deleteTermAnalysis(3);

        verify(archiveTermAnalysisService).deleteById(3, 1);
    }

    private ArchiveController newController() {
        return new ArchiveController(
                archiveTextAnalysisService,
                archiveTermAnalysisService,
                archiveScreen);
    }

    private User user() {
        return new User(1, "Test user", "test@example.com", null, null, "password");
    }
}
