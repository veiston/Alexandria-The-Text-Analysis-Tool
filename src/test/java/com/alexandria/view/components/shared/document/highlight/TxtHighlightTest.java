package com.alexandria.view.components.shared.document.highlight;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TxtHighlightTest {

    @Test
    public void findRanges_nullPageText_returnsEmpty() {
        assertTrue(TxtHighlight.findRanges(null, "term").isEmpty());
    }

    @Test
    public void findRanges_nullTerm_returnsEmpty() {
        assertTrue(TxtHighlight.findRanges("some page text", null).isEmpty());
    }

    @Test
    public void findRanges_blankTerm_returnsEmpty() {
        assertTrue(TxtHighlight.findRanges("some page text", "   ").isEmpty());
    }

    @Test
    public void findRanges_singleMatch_returnsCorrectRange() {
        List<TxtHighlight.Range> ranges = TxtHighlight.findRanges("the quick fox", "quick");
        assertEquals(1, ranges.size());
        assertEquals(new TxtHighlight.Range(4, 9), ranges.get(0));
    }

    @Test
    public void findRanges_multipleMatches_returnsAllRanges() {
        List<TxtHighlight.Range> ranges = TxtHighlight.findRanges("fox fox fox", "fox");
        assertEquals(3, ranges.size());
        assertEquals(new TxtHighlight.Range(0, 3), ranges.get(0));
        assertEquals(new TxtHighlight.Range(4, 7), ranges.get(1));
        assertEquals(new TxtHighlight.Range(8, 11), ranges.get(2));
    }

    @Test
    public void findRanges_isCaseInsensitive() {
        List<TxtHighlight.Range> ranges = TxtHighlight.findRanges("The QUICK Fox", "quick");
        assertEquals(1, ranges.size());
        assertEquals(new TxtHighlight.Range(4, 9), ranges.get(0));
    }

    @Test
    public void findRanges_noMatch_returnsEmpty() {
        assertTrue(TxtHighlight.findRanges("the quick fox", "elephant").isEmpty());
    }
}