package com.alexandria.view.components.archive_screen;

import com.alexandria.model.ArchiveComparison;
import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.Card;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArchiveAnalysisCard extends Card {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM uuuu");

    public ArchiveAnalysisCard(ArchiveTextAnalysis analysis) {
        super(titleText(analysis.getProjectTitle(), analysis.getSourceFileName()));
        LocalizationService i18n = LocalizationService.getInstance();

        setTypeText(i18n.get("textAnalysis"));
        setSourceText(analysis.getSourceFileName());
        setFooterText(i18n.get("archive.saved").replace("{date}", formatDate(analysis.getCreatedAt())));
        setActionText(i18n.get("open"));
    }

    public ArchiveAnalysisCard(ArchiveTermAnalysis analysis) {
        super(titleText(analysis.getProjectTitle(), analysis.getSourceFileName()));
        LocalizationService i18n = LocalizationService.getInstance();

        setTypeText(i18n.get("termAnalysis"));
        setSourceText(analysis.getSourceFileName());
        setFooterText(i18n.get("archive.saved").replace("{date}", formatDate(analysis.getCreatedAt())));
        setActionText(i18n.get("open"));

        Label termLabel = new Label(i18n.get("archive.term").replace("{term}", analysis.getTerm()));
        termLabel.getStyleClass().add("archive-term-preview");
        termLabel.setWrapText(true);
        setExtraContent(termLabel);
    }

    public ArchiveAnalysisCard(ArchiveComparison comparison) {
        super(titleText(comparison.title(), comparison.sources()));
        LocalizationService i18n = LocalizationService.getInstance();

        boolean termComparison = comparison.term() != null;
        setTypeText(termComparison ? i18n.get("termComparison") : i18n.get("textComparison"));
        setSourceText(comparison.sources());
        setFooterText(i18n.get("archive.saved").replace("{date}", formatDate(comparison.createdAt())));
        setActionText(i18n.get("open"));

        if (termComparison) {
            Label termLabel = new Label(i18n.get("archive.term").replace("{term}", comparison.term()));
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
        LocalizationService i18n = LocalizationService.getInstance();
        return date == null ? i18n.get("archive.unknownDate") : DATE_FORMAT.format(date);
    }
}
