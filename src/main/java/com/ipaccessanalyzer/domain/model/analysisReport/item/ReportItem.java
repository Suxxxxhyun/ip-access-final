package com.ipaccessanalyzer.domain.model.analysisReport.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ipaccessanalyzer.application.dto.ParsedLog;
import com.ipaccessanalyzer.domain.enums.FieldSpec;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.BaseSummary;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.RankSummary;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.RatioSummary;

public class ReportItem extends BaseItem {
    private final Map<String, Long> counts = new HashMap<>();

    public ReportItem(FieldSpec fieldSpec) {
        super(fieldSpec);
    }

    public void record(ParsedLog log) {
        String value = this.fieldSpec.extract(log);
        counts.merge(value, 1L, Long::sum);
    }

    // 특정 값에 대한 빈도수 반환
    public long getCountOf(String value) {
        return counts.getOrDefault(value, 0L);
    }

    // 특정 값에 대한 비율 계산 (totalCount 기반)
    public double calculateRatio(String value, long totalCount) {
        if (totalCount == 0) return 0.0;
        return (double) getCountOf(value) / totalCount;
    }

    public List<BaseSummary> getTopRankings(int n) {
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .map(e -> (BaseSummary) RankSummary.of(e.getKey(), e.getValue()))
                .toList();
    }

    public List<BaseSummary> getAllByRatio(long totalCount) {
        return counts.entrySet().stream()
                .map(e -> {
                    String key = e.getKey(); // 실제 데이터 값 (예: "200", "1.1.1.1")
                    long count = e.getValue(); // 발생 횟수
                    double ratio = this.calculateRatio(key, totalCount); // key를 전달해야 함
                    return (BaseSummary) RatioSummary.of(key, count, ratio);
                })
                .toList();
    }
}