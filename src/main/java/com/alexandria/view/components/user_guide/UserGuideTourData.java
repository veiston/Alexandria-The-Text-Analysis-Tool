package com.alexandria.view.components.user_guide;

import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.model.FileType;

import java.time.LocalDateTime;
import java.util.List;

public final class UserGuideTourData {

    public static final String PROJECT_TITLE = "Guided Tour Text";
    public static final String FILE_NAME = "tour.txt";
    public static final FileType FILE_TYPE = FileType.MANUAL;
    public static final String TEXT = """
            Research begins with careful reading. A text can reveal patterns when we look at its words,
            phrases, and repeated ideas. Alexandria helps researchers return to the text, compare evidence,
            and keep useful findings together. Careful reading makes patterns visible, and patterns help
            researchers ask better questions about a text.
            """;

    public static final String QUOTATION =
            "A text can reveal patterns when we look at its words,\nphrases, and repeated ideas.";

    public static final String COMPARISON_FIRST_TITLE = "Guided Tour Text A";
    public static final String COMPARISON_FIRST_FILE_NAME = "tour-text-a.txt";
    public static final String COMPARISON_FIRST_TEXT = """
            Careful reading helps researchers recognise patterns in a text. Repeated words and ideas make
            important themes visible. Alexandria helps researchers compare evidence and return to useful passages.
            """;

    public static final String COMPARISON_SECOND_TITLE = "Guided Tour Text B";
    public static final String COMPARISON_SECOND_FILE_NAME = "tour-text-b.txt";
    public static final String COMPARISON_SECOND_TEXT = """
            Researchers compare texts to recognise repeated patterns and ideas. Careful reading makes shared
            evidence visible and helps researchers return to important passages in each text.
            """;

    public static List<ArchiveTextAnalysis> archiveExamples() {
        ArchiveTextAnalysis example = new ArchiveTextAnalysis(
                null,
                null,
                PROJECT_TITLE,
                FILE_NAME,
                LocalDateTime.now(),
                null);
        return List.of(example, example);
    }

    private UserGuideTourData() {
    }
}
