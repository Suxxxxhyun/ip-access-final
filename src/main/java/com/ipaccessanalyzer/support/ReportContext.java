package com.ipaccessanalyzer.support;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import com.ipaccessanalyzer.application.dto.ParsedLog;
import com.ipaccessanalyzer.domain.enums.FieldSpec;
import com.ipaccessanalyzer.domain.model.analysisReport.item.ReportItem;
import com.ipaccessanalyzer.domain.model.analysisReport.item.SamplingReportItem;

import lombok.Getter;

@Getter
public class ReportContext {
    private UUID reportId;
    private long totalCount = 0;
    private long errorCount = 0;
    
    // 정상 통계 관리
	private final Map<FieldSpec, ReportItem> metrics = new EnumMap<>(FieldSpec.class);
    
    // 특정 필드 혹은 전체 파싱 에러 샘플링 관리
    private final SamplingReportItem errorTracer = FieldSpec.PARSE_ERROR.createSampler();
    
    public ReportContext(UUID reportId) {
        this.reportId = reportId;
        Arrays.stream(FieldSpec.values())
                .forEach(spec -> metrics.put(spec, spec.createSummary()));
    }

    /**
     * 로그 분석 성공 시 호출: 각 필드별 빈도수 합산
     */
    public void accumulate(ParsedLog log) {
        totalCount++;
        metrics.values().forEach(summary -> summary.record(log));
    }

    /**
     * 로그 분석 실패 시 호출: 실패 횟수 카운트 및 원문 샘플 채취
     */
    public void recordParseError(String rawLine) {
        totalCount++;
        errorCount++;
        errorTracer.record(rawLine);
    }

	public SamplingReportItem getErrorTrace() {
        return this.errorTracer;
    }
}