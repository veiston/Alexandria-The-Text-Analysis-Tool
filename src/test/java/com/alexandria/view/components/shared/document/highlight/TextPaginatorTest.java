package com.alexandria.view.components.shared.document.highlight;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TextPaginatorTest {

    @Test
    public void paginate_nullContent_returnsEmptyList() {
        assertTrue(TextPaginator.paginate(null, 10).isEmpty());
    }

    @Test
    public void paginate_emptyContent_returnsEmptyList() {
        assertTrue(TextPaginator.paginate("", 10).isEmpty());
    }

    @Test
    public void paginate_contentShorterThanPageSize_returnsSingleOffset() {
        List<Integer> offsets = TextPaginator.paginate("0123456789", 10);
        assertEquals(List.of(0), offsets);
    }

    @Test
    public void paginate_breaksAtSpace_whenNoParagraphBreakAvailable() {
        // "aaaaaaaaaa bbbbb" — 10 a's, then a space at index 10, then 5 b's
        List<Integer> offsets = TextPaginator.paginate("aaaaaaaaaa bbbbb", 10);
        assertEquals(List.of(0, 10), offsets);
    }

    @Test
    public void paginate_breaksAtParagraph_whenAvailable() {
        // 10 A's, then "\n\n" at index 10-11, then 10 B's
        List<Integer> offsets = TextPaginator.paginate("AAAAAAAAAA\n\nBBBBBBBBBB", 10);
        assertEquals(List.of(0, 10, 20), offsets);
    }

    @Test
    public void paginate_hardBreak_whenNoSpaceOrParagraphAvailable() {
        // no spaces or paragraph breaks anywhere — must fall back to a hard cut
        List<Integer> offsets = TextPaginator.paginate("aaaaaaaaaabbbbbbbbbb", 10);
        assertEquals(List.of(0, 10), offsets);
    }
}