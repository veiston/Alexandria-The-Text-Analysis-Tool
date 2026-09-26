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
