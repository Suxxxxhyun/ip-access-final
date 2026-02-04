package com.ipaccessanalyzer.presentation.dto.response;

import java.util.List;
import java.util.UUID;

import com.ipaccessanalyzer.domain.model.analysisReport.ReportStatistic;

import lombok.Builder;

@Builder
public record AnalysisResultResponse(
	UUID reportId,
	ErrorResponse error
){
	public static AnalysisResultResponse from(ReportStatistic result, ErrorResponse errorResponse) {
		return new AnalysisResultResponse(
			result.getReportId(),
			errorResponse
		);
	}

	public static AnalysisResultResponse from(ReportStatistic result) {
		return new AnalysisResultResponse(
			result.getReportId(),
			null
		);
	}

	public record ErrorResponse(
		long count,
		List<String> samples
	) {
		public static ErrorResponse of(long count, List<String> samples) {
			return new ErrorResponse(count, samples);
		}
	}
}
