package com.ipaccessanalyzer.domain.model.analysisReport.summary;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RankSummary extends BaseSummary {
    
    private RankSummary(String key, long count) {
        super(key, count);
    }

    public static RankSummary of(String key, long count) {
        return new RankSummary(key, count);
    }
}