package com.ipaccessanalyzer.domain.enums;

import java.util.function.Function;

import com.ipaccessanalyzer.application.dto.ParsedLog;
import com.ipaccessanalyzer.application.dto.StatusCode;
import com.ipaccessanalyzer.domain.model.analysisReport.item.ReportItem;
import com.ipaccessanalyzer.domain.model.analysisReport.item.SamplingReportItem;

public enum FieldSpec {
    PATH("호출 경로", ParsedLog::getPath),
    STATUS("응답 상태", log -> StatusCode.of(log.getStatusCode()).name()),
    IP("접속 IP", ParsedLog::getClientIp),
    PARSE_ERROR("파싱 실패 원문", log -> "ERROR");

    private final String description;
    private final Function<ParsedLog, String> extractor;

    FieldSpec(String description, Function<ParsedLog, String> extractor) {
        this.description = description;
        this.extractor = extractor;
    }

    public String extract(ParsedLog log) {
        return extractor.apply(log);
    }
    
    public String getDescription() { return description; }

    public ReportItem createSummary() {
        return new ReportItem(this);
    }

    public SamplingReportItem createSampler() {
        return new SamplingReportItem(this);
    }
}
