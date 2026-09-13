package com.alexandria.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alexandria.service.analysis.SearchMatch;


/*  * Fuzzy Search 
Implementation based on the "Wu-Manber Bitap" -algorithm. 
Heavy bitwise operation madness incoming.

Determines the lowest number of edits additions, changes or removal of letters 
required to transform one string into another. 
A lower Wu-Manber Bitap indicates greater similarity. For instance, 
"sack" and "back" have a "difference" length of 1*/

public class FuzzySearchService {

    public List<SearchMatch> findWithFuzzy(String text, String searchable) {
        List<SearchMatch> matches = new ArrayList<>();

        if (text == null || searchable == null || searchable.isEmpty() || text.isEmpty()) {
            return matches;
        }

        int m = searchable.length();
        if (m > 64) {
            searchable = searchable.substring(0, 64);
            m = 64;
        }

        // Typo budget. Amount of allowed letter differences
        int k;
        if (m >= 8) {
            k = 2;
        } else if (m >= 4) {
            k = 1;
        } else {
            k = 0;
        }

        // Build character masks
        long[] mask = new long[65536];
        Arrays.fill(mask, ~0L);

        for (int i = 0; i < m; i++) {
            char c = Character.toLowerCase(searchable.charAt(i));
            mask[c] &= ~(1L << i);
        }

        // Initialize error states
        long[] R = new long[k + 1];
        for (int d = 0; d <= k; d++) {
            R[d] = ~0L << d;
        }

        long matchBit = 1L << (m - 1);

        // Scan text
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            long charMask = mask[c];

            // Update exact match
            long oldR = R[0];
            R[0] = (R[0] << 1) | charMask;

            for (int d = 1; d <= k; d++) {
                long match      = (R[d] << 1) | charMask;
                long substitute = oldR << 1;
                long insert     = oldR;
                long delete     = R[d - 1] << 1;

                oldR = R[d];
                R[d] = match & substitute & insert & delete;
            }

            // Check if top bit has flipped to 0! Boom: (match found!)
            if ((R[k] & matchBit) == 0L) {
                int start = Math.max(0, i - m + 1);
                int end = i + 1;
                String matchedSnippet = text.substring(start, end);
                String ctx = text.substring(Math.max(0, start - 40), Math.min(text.length(), end + 40)).strip();

                matches.add(new SearchMatch(matchedSnippet, start, end, null, null, ctx));
            }
        }

        return matches;
    }
}
