package com.alexandria.view.components.archive_screen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.service.analysis.WordFrequency;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ArchiveAnalysisModalTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void textAnalysisModalShowsAnalysis() {
        ArchiveAnalysisModal modal = new ArchiveAnalysisModal();

        modal.showTextAnalysis(textAnalysis());

        assertTrue(modal.isShowing());
        assertEquals("Text analysis", ((Label) content(modal).getChildren().get(0)).getText());
    }

    @Test
    public void termAnalysisModalShowsAnalysis() {
        ArchiveAnalysisModal modal = new ArchiveAnalysisModal();

        modal.showTermAnalysis(termAnalysis());

        assertTrue(modal.isShowing());
        VBox header = (VBox) content(modal).getChildren().get(0);
        assertEquals("Term analysis", ((Label) header.getChildren().get(0)).getText());
    }

    private VBox content(ArchiveAnalysisModal modal) {
        VBox dialog = (VBox) modal.getContent();
        ScrollPane scroll = (ScrollPane) dialog.getChildren().get(0);
        return (VBox) scroll.getContent();
    }

    private ArchiveTextAnalysis textAnalysis() {
        TextAnalysisResult result = new TextAnalysisResult(
                10,
                8,
                2,
                1,
                List.of(new WordFrequency("climate", 2, 200.0, null, 1)),
                List.of(
                        new TextFragment("Climate data is changing.", 5, null, 1),
                        new TextFragment("Researchers compare results.", 4, null, 1)));
        return new ArchiveTextAnalysis(1, 1, "Climate", "climate.pdf", null, result);
    }

    private ArchiveTermAnalysis termAnalysis() {
        TermAnalysisResult result = new TermAnalysisResult(
                "climate",
                2,
                200.0,
                2,
                1,
                List.of(new WordFrequency("research", 1, 100.0, null, 1)));
        return new ArchiveTermAnalysis(1, 1, "Climate", "climate.pdf", "climate", null, result);
    }
}
