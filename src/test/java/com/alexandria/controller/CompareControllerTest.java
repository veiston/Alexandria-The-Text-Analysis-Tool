package com.alexandria.controller;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompareControllerTest {
    private CompareController controller;

    @Before
    public void setUp() {
        controller = new CompareController();
    }

    @Test
    public void comparesOpenedTexts() {
        controller.openTexts(List.of(text(1, "First", "fox fox"), text(2, "Second", "fox")));

        CompareController.TermComparisonOutcome outcome = controller.compareTerm("fox");

        assertTrue(outcome.success());
        assertEquals(2, outcome.result().occurrencesPerText().size());
        assertEquals(2, outcome.result().occurrencesPerText().get(0).occurrences());
    }

    @Test
    public void rejectsFewerThanTwoTexts() {
        CompareController.ComparisonTextsOutcome outcome = controller.openTexts(List.of(text(1, "First", "fox")));

        assertFalse(outcome.success());
    }

    @Test
    public void comparesTemporaryGuestTexts() {
        Text firstText = new Text(null, "First", "first.txt", FileType.MANUAL, "fox fox");
        Text secondText = new Text(null, "Second", "second.txt", FileType.MANUAL, "fox");

        CompareController.ComparisonTextsOutcome opened =
                controller.openTexts(List.of(firstText, secondText));
        CompareController.TermComparisonOutcome outcome = controller.compareTerm("fox");

        assertTrue(opened.success());
        assertTrue(outcome.success());
        assertEquals(2, outcome.result().occurrencesPerText().size());
    }

    private Text text(int id, String title, String content) {
        return new Text(id, 1, title, null, null, FileType.MANUAL, content, null);
    }
}
