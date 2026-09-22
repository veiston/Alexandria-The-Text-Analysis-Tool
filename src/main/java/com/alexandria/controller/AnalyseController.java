package com.alexandria.controller;

import com.alexandria.model.Text;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchServiceINT;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TermAnalysisServiceINT;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisServiceINT;
import com.alexandria.view.screens.AnalyseScreen;

import java.io.File;
import java.util.List;

public class AnalyseController {

    private final SearchServiceINT searchService;
    private final TermAnalysisServiceINT termAnalysisService;
    private final TextAnalysisServiceINT textAnalysisService;

    private Text currentText;
    private List<Integer> currentPageOffsets = List.of();

    public AnalyseController(
            SearchServiceINT searchService,
            TermAnalysisServiceINT termAnalysisService,
            TextAnalysisServiceINT textAnalysisService) {

        this.searchService = searchService;
        this.termAnalysisService = termAnalysisService;
        this.textAnalysisService = textAnalysisService;
    }

    public TextAnalysisOutcome openText(Text text, List<Integer> pageOffsets) {
        if (text == null)
            return TextAnalysisOutcome.error("No text was supplied.");

        currentText = text;
        currentPageOffsets = pageOffsets == null ? List.of() : List.copyOf(pageOffsets);

        try {
            return TextAnalysisOutcome.ok(computeTextAnalysis());
        } catch (RuntimeException e) {
            return TextAnalysisOutcome.error(
                    "Could not process text analysis: " + e.getMessage());
        }
    }

    public void configureScreen(
            AnalyseScreen analyseScreen,
            Text text,
            List<Integer> pageOffsets,
            File sourceFile) {

        analyseScreen.loadDocument(
                text.getTitle(),
                text.getFileName(),
                text.getContent(),
                text.getFileType(),
                sourceFile == null ? null : sourceFile.toPath(),
                pageOffsets);

        TextAnalysisOutcome outcome = openText(text, pageOffsets);

        if (!outcome.success()) {
            System.err.println(outcome.message());
            return;
        }

        TextAnalysisResult analysis = outcome.result();

        analyseScreen.setTextAnalysis(
                analysis.frequentWords(),
                analysis.importantFragments());

        configureTermDetail(analyseScreen);
    }

    public SearchOutcome search(
            String term,
            boolean caseSensitive,
            boolean fuzzy,
            boolean wholeWordsOnly) {

        if (currentText == null) {
            return SearchOutcome.error("No text is currently open.");
        }

        if (term == null || term.isBlank()) {
            return SearchOutcome.error("Search term cannot be empty.");
        }

        try {
            SearchSettings settings = new SearchSettings(caseSensitive, fuzzy, wholeWordsOnly);

            List<SearchMatch> matches = searchService.search(
                    currentText.getContent(),
                    term,
                    settings,
                    currentPageOffsets);

            TermAnalysisResult termResult = computeTermAnalysis(term);

            return SearchOutcome.ok(matches, termResult);

        } catch (RuntimeException e) {
            return SearchOutcome.error(
                    "Could not complete search: " + e.getMessage());
        }
    }

    private TextAnalysisResult computeTextAnalysis() {
        TextAnalysisResult result = textAnalysisService.analyzeText(
                currentText.getContent(),
                currentPageOffsets);

        // TODO: Persist text analysis/statistics.
        // Currently analysis is calculated in memory only.
        // IMPORTANT: do not remove in memory it is used for guests

        return result;
    }

    private TermAnalysisResult computeTermAnalysis(String term) {
        TermAnalysisResult result = termAnalysisService.analyzeTerm(
                currentText.getContent(),
                term);

        // TODO: Persist term analysis/statistics in the db.
        // Currently analysis is calculated in memory only.
        // IMPORTANT: do not remove in memory it is used for guests

        return result;
    }

    private void configureTermDetail(AnalyseScreen analyseScreen) {
        analyseScreen.setOnTermDetailRequested(word -> {
            SearchOutcome outcome = search(word, false, false, false);

            if (!outcome.success()) {
                System.err.println(outcome.message());
                return;
            }

            analyseScreen.showTermDetail(
                    word,
                    outcome.termAnalysis(),
                    outcome.matches());
        });
    }

    public record TextAnalysisOutcome(
            boolean success,
            String message,
            TextAnalysisResult result) {

        static TextAnalysisOutcome ok(TextAnalysisResult result) {
            return new TextAnalysisOutcome(true, null, result);
        }

        static TextAnalysisOutcome error(String message) {
            return new TextAnalysisOutcome(false, message, null);
        }
    }

    public record SearchOutcome(
            boolean success,
            String message,
            List<SearchMatch> matches,
            TermAnalysisResult termAnalysis) {

        static SearchOutcome ok(
                List<SearchMatch> matches,
                TermAnalysisResult termAnalysis) {

            return new SearchOutcome(true, null, matches, termAnalysis);
        }

        static SearchOutcome error(String message) {
            return new SearchOutcome(false, message, List.of(), null);
        }
    }
}