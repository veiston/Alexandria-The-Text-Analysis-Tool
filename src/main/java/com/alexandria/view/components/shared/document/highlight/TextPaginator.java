package com.alexandria.view.components.shared.document.highlight;

import java.util.ArrayList;
import java.util.List;

public final class TextPaginator {

    public static final int CHARS_PER_PAGE = 1800;

    private TextPaginator() {
    }

    public static List<Integer> paginate(String content, int charsPerPage) {
        List<Integer> offsets = new ArrayList<>();

        if (content == null || content.isEmpty()) {
            return offsets;
        }

        offsets.add(0);

        int pos = 0;

        while (pos + charsPerPage < content.length()) {
            int candidate = pos + charsPerPage;
            int breakPoint = content.lastIndexOf("\n\n", candidate);

            if (breakPoint <= pos) {
                breakPoint = content.lastIndexOf(' ', candidate);
            }

            if (breakPoint <= pos) {
                breakPoint = candidate;
            }

            pos = breakPoint;
            offsets.add(pos);
        }

        return offsets;
    }
}