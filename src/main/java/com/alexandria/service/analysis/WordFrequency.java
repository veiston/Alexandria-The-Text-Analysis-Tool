package com.alexandria.service.analysis;

public record WordFrequency(String word, int count, double relativeFrequency, Integer page, Integer paragraph) {}