package com.alexandria.view.components.archive_screen;

import com.alexandria.model.ArchiveComparison;
import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.view.components.shared.Card;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArchiveAnalysisCard extends Card {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM uuuu");

    public ArchiveAnalysisCard(ArchiveTextAnalysis analysis) {
        super(titleText(analysis.getProjectTitle(), analysis.getSourceFileName()));

        setTypeText("Text analysis");
        setSourceText(analysis.getSourceFileName());
        setFooterText("Saved " + formatDate(analysis.getCreatedAt()));
        setActionText("Open");
    }

    public ArchiveAnalysisCard(ArchiveTermAnalysis analysis) {
        super(titleText(analysis.getProjectTitle(), analysis.getSourceFileName()));

        setTypeText("Term analysis");
        setSourceText(analysis.getSourceFileName());
        setFooterText("Saved " + formatDate(analysis.getCreatedAt()));
        setActionText("Open");

        Label termLabel = new Label("Term: " + analysis.getTerm());
        termLabel.getStyleClass().add("archive-term-preview");
        termLabel.setWrapText(true);
        setExtraContent(termLabel);
    }

    public ArchiveAnalysisCard(ArchiveComparison comparison) {
        super(titleText(comparison.title(), comparison.sources()));

        boolean termComparison = comparison.term() != null;
        setTypeText(termComparison ? "Term comparison" : "Text comparison");
        setSourceText(comparison.sources());
        setFooterText("Saved " + formatDate(comparison.createdAt()));
        setActionText("Open");

        if (termComparison) {
            Label termLabel = new Label("Term: " + comparison.term());
            termLabel.getStyleClass().add("archive-term-preview");
            termLabel.setWrapText(true);
            setExtraContent(termLabel);
        }
    }

    public Button getOpenButton() {
        return getActionButton();
    }

    private static String titleText(String projectTitle, String sourceFileName) {
        if (projectTitle == null || projectTitle.isBlank()) {
            return sourceFileName;
        }
        return projectTitle;
    }

    private static String formatDate(LocalDateTime date) {
        return date == null ? "unknown date" : DATE_FORMAT.format(date);
    }
}