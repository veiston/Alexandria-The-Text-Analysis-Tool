package com.alexandria.service.analysis;

/**
 * One matched occurrence. matchStart/matchEnd are character offsets into the
 * source text — used by DocumentViewerPanel to build highlighted TextFlow runs.
 */
public record SearchMatch(String text, int matchStart, int matchEnd, Integer page) {}