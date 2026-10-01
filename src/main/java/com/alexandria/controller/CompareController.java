package com.alexandria.controller;

import java.io.File;
import java.util.ArrayList;
import com.alexandria.model.Text;
import com.alexandria.service.TermComparisonService;
import com.alexandria.service.TextComparisonService;
import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TermComparisonServiceINT;
import com.alexandria.service.analysis.TextComparisonResult;
import com.alexandria.service.analysis.TextComparisonServiceINT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareController {
    private final TextComparisonServiceINT textComparisonService;
    private final TermComparisonServiceINT termComparisonService;
    private List<Text> currentTexts = List.of();
    private List<File> currentFiles = List.of();

    public CompareController() {
        this(new TextComparisonService(), new TermComparisonService());
    }

    CompareController(TextComparisonServiceINT textComparisonService, TermComparisonServiceINT termComparisonService) {
        this.textComparisonService = textComparisonService;
        this.termComparisonService = termComparisonService;
    }

    public ComparisonTextsOutcome openTexts(List<Text> texts) {
        return openTexts(texts, null);
    }

    public ComparisonTextsOutcome openTexts(List<Text> texts, List<File> files) {
        try {
            validateTexts(texts);
            validateFiles(texts, files);
            currentTexts = List.copyOf(texts);
            currentFiles = files == null ? List.of() : new ArrayList<>(files);
            return ComparisonTextsOutcome.ok(currentTexts);
        } catch (IllegalArgumentException e) {
            currentTexts = List.of();
            currentFiles = List.of();
            return ComparisonTextsOutcome.error(e.getMessage());
        }
    }

    public List<File> getCurrentFiles() {
        return new ArrayList<>(currentFiles);
    }

    public TextComparisonOutcome compareTexts(int limit) {
        if (currentTexts.isEmpty()) {
            return TextComparisonOutcome.error("No texts are currently selected for comparison.");
        }

        Map<Integer, String> contentsByTextId = new LinkedHashMap<>();
        for (int index = 0; index < currentTexts.size(); index++) {
            Text text = currentTexts.get(index);
            contentsByTextId.put(comparisonTextId(text, index), text.getContent());
        }
        return TextComparisonOutcome.ok(textComparisonService.compareTexts(contentsByTextId, limit));
    }

    public TermComparisonOutcome compareTerm(String term) {
        return compareTerm(term, false);
    }

    public TermComparisonOutcome compareTerm(String term, boolean ignoreStopWords) {
        if (currentTexts.isEmpty()) {
            return TermComparisonOutcome.error("No texts are currently selected for comparison.");
        }

        try {
            Map<Integer, String> contentsByTextId = new LinkedHashMap<>();
            Map<Integer, String> titlesByTextId = new HashMap<>();
            for (int index = 0; index < currentTexts.size(); index++) {
                Text text = currentTexts.get(index);
                int textId = comparisonTextId(text, index);
                contentsByTextId.put(textId, text.getContent());
                titlesByTextId.put(textId, text.getTitle());
            }
            return TermComparisonOutcome.ok(
                    termComparisonService.compareTerm(contentsByTextId, titlesByTextId, term, ignoreStopWords));
        } catch (IllegalArgumentException e) {
            return TermComparisonOutcome.error(e.getMessage());
        }
    }

    private void validateTexts(List<Text> texts) {
        if (texts == null || texts.size() < 2) {
            throw new IllegalArgumentException("Select at least two texts to compare.");
        }

        Set<Integer> textIds = new HashSet<>();
        for (Text text : texts) {
            if (text == null) {
                throw new IllegalArgumentException("Select valid texts to compare.");
            }
            if (text.getId() != null && !textIds.add(text.getId())) {
                throw new IllegalArgumentException("Selected texts must be unique.");
            }
        }
    }

    private void validateFiles(List<Text> texts, List<File> files) {
        if (files == null) {
            return;
        }
        if (files.size() != texts.size()) {
            throw new IllegalArgumentException("A source file is required for every selected text.");
        }

        for (int index = 0; index < texts.size(); index++) {
            if (texts.get(index).getFileType() != com.alexandria.model.FileType.MANUAL
                    && files.get(index) == null) {
                throw new IllegalArgumentException("A source file is required for every selected text.");
            }
        }
    }

    private int comparisonTextId(Text text, int index) {
        return text.getId() == null ? -index - 1 : text.getId();
    }

    public record ComparisonTextsOutcome(boolean success, String message, List<Text> texts) {
        static ComparisonTextsOutcome ok(List<Text> texts) {
            return new ComparisonTextsOutcome(true, null, List.copyOf(texts));
        }

        static ComparisonTextsOutcome error(String message) {
            return new ComparisonTextsOutcome(false, message, List.of());
        }
    }

    public record TextComparisonOutcome(boolean success, String message, TextComparisonResult result) {
        static TextComparisonOutcome ok(TextComparisonResult result) {
            return new TextComparisonOutcome(true, null, result);
        }

        static TextComparisonOutcome error(String message) {
            return new TextComparisonOutcome(false, message, null);
        }
    }

    public record TermComparisonOutcome(boolean success, String message, TermComparisonResult result) {
        static TermComparisonOutcome ok(TermComparisonResult result) {
            return new TermComparisonOutcome(true, null, result);
        }

        static TermComparisonOutcome error(String message) {
            return new TermComparisonOutcome(false, message, null);
        }
    }
}
