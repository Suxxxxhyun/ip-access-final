package com.ipaccessanalyzer.domain.model.analysisReport.summary;

import com.ipaccessanalyzer.application.dto.StatusCode;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RatioSummary extends BaseSummary {
    private final double ratio;

    private RatioSummary(String key, long count, double ratio) {
        super(key, count);
        this.ratio = ratio;
    }

    // 상태 코드를 위한 정적 팩토리 메서드
    public static RatioSummary of(StatusCode statusCode, long count, double ratio) {
        return new RatioSummary(statusCode.name(), count, ratio);
    }
    
    // 일반 문자열 키를 위한 메서드
    public static RatioSummary of(String key, long count, double ratio) {
        return new RatioSummary(key, count, ratio);
    }
}