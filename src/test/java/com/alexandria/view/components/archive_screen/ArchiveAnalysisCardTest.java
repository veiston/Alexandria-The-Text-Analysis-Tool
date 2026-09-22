package com.alexandria.view.components.archive_screen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

public class ArchiveAnalysisCardTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void textAnalysisCardShowsProjectAndSource() {
        ArchiveAnalysisCard card = new ArchiveAnalysisCard(textAnalysis());
        VBox details = (VBox) card.getChildren().get(1);

        assertEquals("Climate Research", ((Label) details.getChildren().get(0)).getText());
        assertEquals("climate-research.pdf", ((Label) details.getChildren().get(1)).getText());
    }

    @Test
    public void termAnalysisCardShowsTerm() {
        ArchiveAnalysisCard card = new ArchiveAnalysisCard(termAnalysis());
        VBox details = (VBox) card.getChildren().get(1);

        assertEquals("Term: climate", ((Label) details.getChildren().get(1)).getText());
        assertEquals("unknown date", ((Label) ((javafx.scene.layout.HBox) card.getChildren().get(3))
                .getChildren().get(1)).getText().replace("Saved ", ""));
    }

    @Test
    public void cardButtonsCanBeUsed() {
        ArchiveAnalysisCard card = new ArchiveAnalysisCard(textAnalysis());
        boolean[] deleted = { false };
        boolean[] opened = { false };

        card.getDeleteButton().setOnAction(event -> deleted[0] = true);
        card.getOpenButton().setOnAction(event -> opened[0] = true);
        card.getDeleteButton().fire();
        card.getOpenButton().fire();

        assertTrue(deleted[0]);
        assertTrue(opened[0]);
    }

    private ArchiveTextAnalysis textAnalysis() {
        TextAnalysisResult result = new TextAnalysisResult(10, 8, 2, 1, List.of(), List.of());
        return new ArchiveTextAnalysis(
                1,
                1,
                "Climate Research",
                "climate-research.pdf",
                LocalDateTime.now(),
                result);
    }

    private ArchiveTermAnalysis termAnalysis() {
        TermAnalysisResult result = new TermAnalysisResult("climate", 2, 100.0, 2, 1, List.of());
        return new ArchiveTermAnalysis(
                2,
                1,
                "",
                "climate-research.pdf",
                "climate",
                null,
                result);
    }
}
