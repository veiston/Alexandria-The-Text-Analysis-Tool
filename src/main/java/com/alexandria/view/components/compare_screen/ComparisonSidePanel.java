package com.alexandria.view.components.compare_screen;

import com.alexandria.service.LocalizationService;
import com.alexandria.service.analysis.TextComparisonResult;
import com.alexandria.view.components.shared.search.SearchView;
import com.alexandria.view.components.shared.FrequencyBar;
import com.alexandria.view.components.shared.ContextMatchCard;
import com.alexandria.view.components.shared.PassagePreview;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Comparison-specific result panels built with the same search, tracker and
 * card patterns as AnalyseScreen.
 */
public class ComparisonSidePanel extends VBox {
    private final CommonWordsPanel commonWordsPanel = new CommonWordsPanel();
    private final SearchView searchView = new SearchView();
    private final SimilarParagraphsPanel paragraphsPanel = new SimilarParagraphsPanel();
    private final Label searchHeading = new Label();

    private Consumer<String> onCommonWordSelected = word -> {
    };
    private Consumer<TextComparisonResult.ParagraphSnippet> onDocumentAParagraphSelected = snippet -> {
    };
    private Consumer<TextComparisonResult.ParagraphSnippet> onDocumentBParagraphSelected = snippet -> {
    };

    public ComparisonSidePanel() {
        getStyleClass().add("comparison-side-panel");
        setSpacing(16);
        setPadding(new Insets(0));
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        searchHeading.getStyleClass().add("heading-sm");

        searchView.setPromptKey("compare.searchBothTexts");

        VBox searchCard = new VBox(12, searchHeading, searchView);
        searchCard.getStyleClass().add("card");
        searchCard.setPadding(new Insets(14));

        getChildren().addAll(commonWordsPanel, searchCard, paragraphsPanel);

        commonWordsPanel.setOnWordSelected(word -> onCommonWordSelected.accept(word));
        paragraphsPanel.setOnDocumentASelected(snippet -> onDocumentAParagraphSelected.accept(snippet));
        paragraphsPanel.setOnDocumentBSelected(snippet -> onDocumentBParagraphSelected.accept(snippet));

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        searchHeading.setText(i18n.get("compare.searchBothTexts"));
        commonWordsPanel.refreshText();
        paragraphsPanel.refreshText();
        searchView.refreshText();
    }

    public void setTextComparison(TextComparisonResult result) {
        commonWordsPanel.setResult(result);
        paragraphsPanel.setResults(result == null ? List.of() : result.similarParagraphs());
    }

    public void setResults(TextComparisonResult result) {
        setTextComparison(result);
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public Node getCommonWordsPanel() {
        return commonWordsPanel;
    }

    public Node getSimilarParagraphsPanel() {
        return paragraphsPanel;
    }

    public void setOnCommonWordSelected(Consumer<String> handler) {
        onCommonWordSelected = handler == null ? word -> {
        } : handler;
    }

    public void setOnDocumentAParagraphSelected(Consumer<TextComparisonResult.ParagraphSnippet> handler) {
        onDocumentAParagraphSelected = handler == null ? snippet -> {
        } : handler;
    }

    public void setOnDocumentBParagraphSelected(Consumer<TextComparisonResult.ParagraphSnippet> handler) {
        onDocumentBParagraphSelected = handler == null ? snippet -> {
        } : handler;
    }

    public void clear() {
        commonWordsPanel.setResult(null);
        paragraphsPanel.setResults(List.of());
        searchView.reset();
    }

    private static final class CommonWordsPanel extends VBox {
        private final VBox rowsHost = new VBox(10);
        private final Label headingLabel = new Label();
        private final Label emptyLabel = new Label();
        private TextComparisonResult currentResult;
        private Consumer<String> onWordSelected = word -> {
        };

        private CommonWordsPanel() {
            getStyleClass().add("card");
            setSpacing(12);
            setPadding(new Insets(14));
            headingLabel.getStyleClass().add("heading-sm");
            emptyLabel.getStyleClass().add("text-muted");
            getChildren().addAll(headingLabel, rowsHost);
            refreshText();
            showEmpty();
        }

        private void refreshText() {
            LocalizationService i18n = LocalizationService.getInstance();
            headingLabel.setText(i18n.get("compare.mostFrequentSharedWords"));
            emptyLabel.setText(i18n.get("compare.noSharedWords"));
            setResult(currentResult);
        }

        private void setResult(TextComparisonResult result) {
            this.currentResult = result;
            rowsHost.getChildren().clear();
            if (result == null || result.commonWords().isEmpty()) {
                showEmpty();
                return;
            }

            int maxCount = result.commonWords().stream()
                    .mapToInt(CommonWordsPanel::totalCount)
                    .max()
                    .orElse(1);
            for (TextComparisonResult.TextComparisonRow word : result.commonWords()) {
                rowsHost.getChildren().add(buildRow(word, maxCount, result.textIds()));
            }
        }

        private HBox buildRow(
                TextComparisonResult.TextComparisonRow word,
                int maxCount,
                List<Integer> textIds) {
            Label termLabel = new Label(word.word());
            termLabel.getStyleClass().add("term-frequency-label");
            termLabel.setMinWidth(72);

            FrequencyBar frequencyBar = new FrequencyBar();
            frequencyBar.setRatio((double) totalCount(word) / Math.max(1, maxCount));
            HBox.setHgrow(frequencyBar, Priority.ALWAYS);

            Label countLabel = new Label(countsForDocuments(word, textIds));
            countLabel.getStyleClass().add("text-muted");
            HBox row = new HBox(10, termLabel, frequencyBar, countLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("frequency-row");
            row.setOnMouseClicked(event -> onWordSelected.accept(word.word()));
            return row;
        }

        private static int totalCount(TextComparisonResult.TextComparisonRow row) {
            return row.countsByTextId().values().stream().mapToInt(Integer::intValue).sum();
        }

        private static String countsForDocuments(
                TextComparisonResult.TextComparisonRow row,
                List<Integer> textIds) {
            if (textIds == null || textIds.isEmpty()) {
                return String.valueOf(totalCount(row));
            }
            LocalizationService i18n = LocalizationService.getInstance();
            StringBuilder counts = new StringBuilder();
            for (int index = 0; index < textIds.size(); index++) {
                if (index > 0) {
                    counts.append(" · ");
                }
                String docName = index == 0 ? i18n.get("compare.documentAName") : i18n.get("compare.documentBName");
                counts.append(docName).append(": ")
                        .append(i18n.formatNumber(row.countsByTextId().getOrDefault(textIds.get(index), 0)));
            }
            return counts.toString();
        }

        private void setOnWordSelected(Consumer<String> handler) {
            onWordSelected = handler;
        }

        private void showEmpty() {
            rowsHost.getChildren().setAll(emptyLabel);
        }
    }

    private static final class SimilarParagraphsPanel extends VBox {
        private static final int SNIPPET_CHAR_BUDGET = 100;

        private final VBox cardsHost = new VBox(10);
        private final Label headingLabel = new Label();
        private final Label emptyLabel = new Label();
        private List<TextComparisonResult.ParagraphMatch> currentMatches = List.of();
        private Consumer<TextComparisonResult.ParagraphSnippet> onDocumentASelected = snippet -> {
        };
        private Consumer<TextComparisonResult.ParagraphSnippet> onDocumentBSelected = snippet -> {
        };

        private SimilarParagraphsPanel() {
            getStyleClass().add("comparison-key-paragraphs");
            setSpacing(12);
            headingLabel.getStyleClass().add("heading-sm");
            emptyLabel.getStyleClass().add("text-muted");
            getChildren().addAll(headingLabel, cardsHost);
            refreshText();
            showEmpty();
        }

        private void refreshText() {
            LocalizationService i18n = LocalizationService.getInstance();
            headingLabel.setText(i18n.get("compare.keyParagraphs"));
            emptyLabel.setText(i18n.get("compare.noSimilarParagraphs"));
            setResults(currentMatches);
        }

        private void setResults(List<TextComparisonResult.ParagraphMatch> matches) {
            this.currentMatches = matches == null ? List.of() : List.copyOf(matches);
            cardsHost.getChildren().clear();
            if (currentMatches.isEmpty()) {
                showEmpty();
                return;
            }
            for (TextComparisonResult.ParagraphMatch match : currentMatches) {
                cardsHost.getChildren().add(buildCard(match));
            }
        }

        private VBox buildCard(TextComparisonResult.ParagraphMatch match) {
            LocalizationService i18n = LocalizationService.getInstance();
            Label score = new Label(i18n.get("compare.similarityScore")
                    .replace("{score}", String.format("%.0f", match.scorePercent())));
            score.getStyleClass().add("context-match-page");

            ContextMatchCard card = new ContextMatchCard(
                    score,
                    documentPassage(i18n.get("compare.documentAName"), match.first(), onDocumentASelected),
                    documentPassage(i18n.get("compare.documentBName"), match.second(), onDocumentBSelected));
            card.setSpacing(7);
            return card;
        }

        private VBox documentPassage(
                String documentName,
                TextComparisonResult.ParagraphSnippet snippet,
                Consumer<TextComparisonResult.ParagraphSnippet> onGoTo) {
            return new PassagePreview(
                    documentName + " · " + formatLocation(snippet),
                    truncate(snippet.text()),
                    () -> onGoTo.accept(snippet));
        }

        private void setOnDocumentASelected(Consumer<TextComparisonResult.ParagraphSnippet> handler) {
            onDocumentASelected = handler;
        }

        private void setOnDocumentBSelected(Consumer<TextComparisonResult.ParagraphSnippet> handler) {
            onDocumentBSelected = handler;
        }

        private void showEmpty() {
            cardsHost.getChildren().setAll(emptyLabel);
        }

        private static String formatLocation(TextComparisonResult.ParagraphSnippet snippet) {
            LocalizationService i18n = LocalizationService.getInstance();
            if (snippet.page() != null && snippet.paragraphIndex() > 0) {
                return i18n.get("compare.pageAndParagraph")
                        .replace("{page}", String.valueOf(snippet.page()))
                        .replace("{paragraph}", String.valueOf(snippet.paragraphIndex()));
            }
            if (snippet.page() != null) {
                return i18n.get("compare.page").replace("{page}", String.valueOf(snippet.page()));
            }
            return i18n.get("compare.paragraph").replace("{paragraph}", String.valueOf(snippet.paragraphIndex()));
        }

        private static String truncate(String text) {
            if (text == null) {
                return "";
            }
            String flat = text.replaceAll("\\s+", " ").strip();
            if (flat.length() <= SNIPPET_CHAR_BUDGET) {
                return flat;
            }
            int cut = flat.lastIndexOf(' ', SNIPPET_CHAR_BUDGET);
            return flat.substring(0, cut <= 0 ? SNIPPET_CHAR_BUDGET : cut).stripTrailing() + "...";
        }
    }
}
