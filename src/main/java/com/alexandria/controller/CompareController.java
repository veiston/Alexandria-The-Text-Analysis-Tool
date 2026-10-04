package com.alexandria.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alexandria.dao.TermComparisonDAO;
import com.alexandria.dao.TermComparisonTextDAO;
import com.alexandria.dao.TextComparisonDAO;
import com.alexandria.dao.TextComparisonTextDAO;
import com.alexandria.model.Text;
import com.alexandria.model.TermComparison;
import com.alexandria.model.TermComparisonText;
import com.alexandria.model.TextComparison;
import com.alexandria.model.TextComparisonText;
import com.alexandria.model.User;
import com.alexandria.service.PdfService;
import com.alexandria.service.TermComparisonService;
import com.alexandria.service.TextComparisonService;
import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TermComparisonServiceINT;
import com.alexandria.service.analysis.TextComparisonResult;
import com.alexandria.service.analysis.TextComparisonServiceINT;
import com.alexandria.utils.JsonMapper;
import com.alexandria.view.components.shared.document.highlight.TextPaginator;

public class CompareController {
    public static final int COMMON_WORDS_LIMIT = 5;

    private final TextComparisonServiceINT textComparisonService;
    private final TermComparisonServiceINT termComparisonService;
    private final com.alexandria.service.analysis.SearchServiceINT searchService;
    private List<Text> currentTexts = List.of();
    private List<File> currentFiles = List.of();
    private TextComparisonResult currentTextComparison;

    private final PdfService pdfService = new PdfService();
    private final Map<Integer, List<Integer>> currentPageOffsetsById = new HashMap<>();
    private final UserSessionController session = UserSessionController.getInstance();
    private final TextComparisonDAO textComparisonDAO = new TextComparisonDAO();
    private final TextComparisonTextDAO textComparisonTextDAO = new TextComparisonTextDAO();
    private final TermComparisonDAO termComparisonDAO = new TermComparisonDAO();
    private final TermComparisonTextDAO termComparisonTextDAO = new TermComparisonTextDAO();

    public CompareController() {
        this(new TextComparisonService(), new TermComparisonService(), new com.alexandria.service.SearchService());
    }

    CompareController(TextComparisonServiceINT textComparisonService, TermComparisonServiceINT termComparisonService) {
        this(textComparisonService, termComparisonService, new com.alexandria.service.SearchService());
    }

    CompareController(TextComparisonServiceINT textComparisonService, TermComparisonServiceINT termComparisonService,
            com.alexandria.service.analysis.SearchServiceINT searchService) {
        this.textComparisonService = textComparisonService;
        this.termComparisonService = termComparisonService;
        this.searchService = searchService;
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
            currentTextComparison = null;

            // Cache page data to avoid constant re-parsing of the files.
            currentPageOffsetsById.clear();
            for (int index = 0; index < currentTexts.size(); index++) {
                Text text = currentTexts.get(index);
                File file = null;
                if (index < currentFiles.size()) {
                    file = currentFiles.get(index);
                }
                List<Integer> offsets = TextPaginator.paginate(
                        text.getContent(), TextPaginator.CHARS_PER_PAGE);
                if (text.getFileType() != com.alexandria.model.FileType.MANUAL && file != null) {
                    try {
                        offsets = pdfService.extractTextWithPageBoundaries(file).pageOffsets();
                    } catch (Exception e) {
                        // Stored content is still navigable if the original PDF is unavailable.
                    }
                }
                currentPageOffsetsById.put(comparisonTextId(text, index), offsets);
            }

            return ComparisonTextsOutcome.ok(currentTexts);
        } catch (Exception e) {
            currentTexts = List.of();
            currentFiles = List.of();
            currentTextComparison = null;
            currentPageOffsetsById.clear();
            return ComparisonTextsOutcome.error(e.getMessage());
        }
    }

    public List<File> getCurrentFiles() {
        return new ArrayList<>(currentFiles);
    }

    public List<Text> getCurrentTexts() {
        return List.copyOf(currentTexts);
    }

    public TextComparisonOutcome compareTexts(int limit) {
        if (currentTexts.isEmpty()) {
            return TextComparisonOutcome.error("No texts currently selected for comparison.");
        }

        Map<Integer, String> contentsByTextId = new LinkedHashMap<>();
        for (int index = 0; index < currentTexts.size(); index++) {
            Text text = currentTexts.get(index);
            contentsByTextId.put(comparisonTextId(text, index), text.getContent());
        }
        currentTextComparison = textComparisonService.compareTexts(contentsByTextId, currentPageOffsetsById, limit);
        return TextComparisonOutcome.ok(currentTextComparison);
    }

    public TermComparisonOutcome compareTerm(String term) {
        return compareTerm(term, false);
    }

    public TermComparisonOutcome compareTerm(String term, boolean ignoreStopWords) {
        if (currentTexts.isEmpty()) {
            return TermComparisonOutcome.error("No texts currently selected for comparison.");
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

    public MultiSearchOutcome search(String term, com.alexandria.service.analysis.SearchSettings settings) {
        if (currentTexts.isEmpty()) {
            return MultiSearchOutcome.error("No texts currently selected.");
        }
        try {
            Map<Integer, String> contentsByTextId = new LinkedHashMap<>();
            for (int index = 0; index < currentTexts.size(); index++) {
                Text text = currentTexts.get(index);
                contentsByTextId.put(comparisonTextId(text, index), text.getContent());
            }
            Map<Integer, List<com.alexandria.service.analysis.SearchMatch>> matches = searchService
                    .searchMultiple(contentsByTextId, term, settings, currentPageOffsetsById);
            return MultiSearchOutcome.ok(matches);
        } catch (IllegalArgumentException e) {
            return MultiSearchOutcome.error(e.getMessage());
        }
    }

    /**
     * Saves the text comparison (similarity + shared words + key paragraphs) and
     * one term
     * comparison per tracked term, each linked to both compared texts.
     * Both texts must be saved in the logged-in user's library.
     */
    public SaveOutcome saveFindings(Collection<String> trackedTerms) {
        User user = session.getCurrentUser();
        if (user == null) {
            return SaveOutcome.error("Log in to save comparison findings.");
        }
        if (currentTexts.size() < 2) {
            return SaveOutcome.error("No texts currently selected for comparison.");
        }
        for (Text text : currentTexts) {
            if (text.getId() == null || !java.util.Objects.equals(user.getId(), text.getUserId())) {
                return SaveOutcome.error("Both texts must be saved in your library to save findings.");
            }
        }

        List<String> terms = trackedTerms == null ? List.of()
                : trackedTerms.stream()
                        .filter(term -> term != null && !term.isBlank())
                        .distinct()
                        .toList();

        try {
            if (currentTextComparison == null) {
                TextComparisonOutcome comparison = compareTexts(COMMON_WORDS_LIMIT);
                if (!comparison.success()) {
                    return SaveOutcome.error(comparison.message());
                }
            }

            int saved = 0;

            TextComparison textComparison = textComparisonDAO.create(
                    new TextComparison(user.getId(), JsonMapper.toJson(currentTextComparison)));
            for (Text text : currentTexts) {
                textComparisonTextDAO.create(new TextComparisonText(textComparison.getId(), text.getId()));
            }
            saved++;

            for (String term : terms) {
                TermComparisonOutcome comparison = compareTerm(term);
                if (!comparison.success()) {
                    return SaveOutcome.error(comparison.message());
                }
                TermComparison termComparison = termComparisonDAO.create(
                        new TermComparison(user.getId(), term, JsonMapper.toJson(comparison.result())));
                for (Text text : currentTexts) {
                    termComparisonTextDAO.create(new TermComparisonText(termComparison.getId(), text.getId()));
                }
                saved++;
            }
            return SaveOutcome.ok(saved);
        } catch (Exception e) {
            return SaveOutcome.error("Could not save findings: " + e.getMessage());
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

    /** savedCount = the text comparison plus every saved term comparison. */
    public record SaveOutcome(boolean success, String message, int savedCount) {
        static SaveOutcome ok(int savedCount) {
            return new SaveOutcome(true, null, savedCount);
        }

        static SaveOutcome error(String message) {
            return new SaveOutcome(false, message, 0);
        }
    }

    public record MultiSearchOutcome(boolean success, String message,
            Map<Integer, List<com.alexandria.service.analysis.SearchMatch>> matches) {
        public int totalMatches() {
            int sum = 0;
            for (List<com.alexandria.service.analysis.SearchMatch> list : matches.values()) {
                sum += list.size();
            }
            return sum;
        }

        public int matchCount(int textId) {
            List<com.alexandria.service.analysis.SearchMatch> list = matches.get(textId);
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        static MultiSearchOutcome ok(Map<Integer, List<com.alexandria.service.analysis.SearchMatch>> matches) {
            if (matches != null) {
                return new MultiSearchOutcome(true, null, Map.copyOf(matches));
            }
            return new MultiSearchOutcome(true, null, Map.of());
        }

        static MultiSearchOutcome error(String message) {
            return new MultiSearchOutcome(false, message, Map.of());
        }
    }
}