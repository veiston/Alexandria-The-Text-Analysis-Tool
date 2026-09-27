package com.alexandria.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchSettings;


/*  * Fuzzy Search 
Implementation based on the "Wu-Manber Bitap" -algorithm. 
Heavy bitwise operation madness incoming.

Determines the lowest number of edits additions, changes or removal of letters 
required to transform one string into another. 
A lower Wu-Manber Bitap indicates greater similarity. For instance, 
"sack" and "back" have a "difference" length of 1*/

public class FuzzySearchService {

    public List<SearchMatch> findWithFuzzy(String text, String searchable) {
        return findWithFuzzy(text, searchable, null, null, null);
    }

    public List<SearchMatch> findWithFuzzy(
            String text,
            String searchable,
            List<Integer> pageOffsets,
            List<Integer> paragraphOffsets) {
        return findWithFuzzy(text, searchable, null, pageOffsets, paragraphOffsets);
    }

    public List<SearchMatch> findWithFuzzy(
            String text,
            String searchable,
            SearchSettings setting,
            List<Integer> pageOffsets,
            List<Integer> paragraphOffsets) {
        List<SearchMatch> matches = new ArrayList<>();

        if (text == null || searchable == null || searchable.isEmpty() || text.isEmpty()) {
            return matches;
        }

        boolean caseSensitive = false;
        boolean wholeWordsOnly = false;
        if (setting != null) {
            caseSensitive = setting.caseSensitive();
            wholeWordsOnly = setting.wholeWordsOnly();
        }

        int m = searchable.length();
        if (m > 64) {
            searchable = searchable.substring(0, 64);
            m = 64;
        }

        // Typo budget. Amount of allowed letter differences
        // TODO: Add the ability to change the "budget" from the UI?
        int k;
        if (m >= 5) {
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
            char c = searchable.charAt(i);
            if (!caseSensitive) {
                c = Character.toLowerCase(c);
            }
            mask[c] &= ~(1L << i);
        }

        // Initialize error states
        long[] R = new long[k + 1];
        for (int d = 0; d <= k; d++) {
            R[d] = ~0L << d;
        }

        long matchBit = 1L << (m - 1);

        // Scan text and note word startpoint
        int wordStart = 0;
        int bestMatchEnd = -1;
        int bestError = Integer.MAX_VALUE;

        for (int i = 0; i <= text.length(); i++) {
            boolean atEnd = (i == text.length() || !Character.isLetterOrDigit(text.charAt(i)));
            if (atEnd) {
                if (i > wordStart) {
                    if (wholeWordsOnly) {
                        if (Math.abs((i - wordStart) - m) <= k && (R[k] & matchBit) == 0L) {
                            String matchedSnippet = text.substring(wordStart, i);
                            String ctx = text.substring(Math.max(0, wordStart - 40), Math.min(text.length(), i + 40)).strip();
                            Integer page = AnalysisUtils.resolvePage(wordStart, pageOffsets);
                            Integer paragraph = AnalysisUtils.resolveParagraph(wordStart, paragraphOffsets);
                            matches.add(new SearchMatch(matchedSnippet, wordStart, i, page, paragraph, ctx));
                        }
                    } else {
                        if (Math.abs((i - wordStart) - m) <= k && (R[k] & matchBit) == 0L) {
                            String matchedSnippet = text.substring(wordStart, i);
                            String ctx = text.substring(Math.max(0, wordStart - 40), Math.min(text.length(), i + 40)).strip();
                            Integer page = AnalysisUtils.resolvePage(wordStart, pageOffsets);
                            Integer paragraph = AnalysisUtils.resolveParagraph(wordStart, paragraphOffsets);
                            matches.add(new SearchMatch(matchedSnippet, wordStart, i, page, paragraph, ctx));
                        } else if (bestMatchEnd > wordStart && bestError <= k) {
                            int matchStart = findBestStart(text, wordStart, bestMatchEnd, searchable, bestError, caseSensitive);
                            String matchedSnippet = text.substring(matchStart, bestMatchEnd);
                            String ctx = text.substring(Math.max(0, matchStart - 40), Math.min(text.length(), bestMatchEnd + 40)).strip();
                            Integer page = AnalysisUtils.resolvePage(matchStart, pageOffsets);
                            Integer paragraph = AnalysisUtils.resolveParagraph(matchStart, paragraphOffsets);
                            matches.add(new SearchMatch(matchedSnippet, matchStart, bestMatchEnd, page, paragraph, ctx));
                        }
                    }
                }
                for (int d = 0; d <= k; d++) {
                    R[d] = ~0L << d;
                }
                wordStart = i + 1;
                bestMatchEnd = -1;
                bestError = Integer.MAX_VALUE;
                continue;
            }

            char c = text.charAt(i);
            if (!caseSensitive) {
                c = Character.toLowerCase(c);
            }
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

            if (!wholeWordsOnly) {
                for (int d = 0; d <= k; d++) {
                    if ((R[d] & matchBit) == 0L) {
                        if (d < bestError || (d == bestError && Math.abs((i + 1 - wordStart) - m) < Math.abs((bestMatchEnd - wordStart) - m))) {
                            bestError = d;
                            bestMatchEnd = i + 1;
                        }
                        break;
                    }
                }
            }
        }

        return matches;
    }

    private int findBestStart(String text, int wordStart, int matchEnd, String searchable, int d, boolean caseSensitive) {
        int m = searchable.length();
        if (d == 0) {
            return Math.max(wordStart, matchEnd - m);
        }
        int bestStart = Math.max(wordStart, matchEnd - m);
        int bestDist = Integer.MAX_VALUE;

        int minLen = Math.max(1, m - d);
        int maxLen = m + d;

        for (int len = minLen; len <= maxLen; len++) {
            int start = matchEnd - len;
            if (start < wordStart) {
                continue;
            }
            String candidate = text.substring(start, matchEnd);
            int dist = computeEditDistance(candidate, searchable, caseSensitive);
            if (dist < bestDist) {
                bestDist = dist;
                bestStart = start;
            } else if (dist == bestDist) {
                if (Math.abs(len - m) < Math.abs((matchEnd - bestStart) - m)) {
                    bestStart = start;
                }
            }
        }
        return bestStart;
    }

    private int computeEditDistance(String s1, String s2, boolean caseSensitive) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];

        for (int j = 0; j <= len2; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            curr[0] = i;
            char c1 = s1.charAt(i - 1);
            if (!caseSensitive) {
                c1 = Character.toLowerCase(c1);
            }
            for (int j = 1; j <= len2; j++) {
                char c2 = s2.charAt(j - 1);
                if (!caseSensitive) {
                    c2 = Character.toLowerCase(c2);
                }
                int cost = 1;
                if (c1 == c2) {
                    cost = 0;
                }
                int insert = curr[j - 1] + 1;
                int delete = prev[j] + 1;
                int substitute = prev[j - 1] + cost;
                curr[j] = Math.min(Math.min(insert, delete), substitute);
            }
            System.arraycopy(curr, 0, prev, 0, len2 + 1);
        }
        return prev[len2];
    }
}
