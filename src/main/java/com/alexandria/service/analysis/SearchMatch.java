package com.alexandria.service.analysis;

/**
 * One matched occurrence. matchStart/matchEnd are character indexes of the source text. We can use this for highlighting
 */
public record SearchMatch(String text, int matchStart, int matchEnd, Integer page, Integer paragraph, String context) {}