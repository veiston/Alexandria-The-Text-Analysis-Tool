package com.alexandria.model;

import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TextComparisonResult;

import java.time.LocalDateTime;

public record ArchiveComparison(
                Integer id,
                String title,
                String sources,
                String term,
                LocalDateTime createdAt,
                TextComparisonResult textResult,
                TermComparisonResult termResult) {
}