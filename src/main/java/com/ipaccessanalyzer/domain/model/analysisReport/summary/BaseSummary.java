package com.ipaccessanalyzer.domain.model.analysisReport.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public abstract class BaseSummary {
    private final String key;
    private final long count;
}