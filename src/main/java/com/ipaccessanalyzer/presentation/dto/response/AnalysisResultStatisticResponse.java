package com.ipaccessanalyzer.presentation.dto.response;

import java.util.List;
import java.util.stream.IntStream;

import com.ipaccessanalyzer.application.dto.EnrichedIpCount;
import com.ipaccessanalyzer.application.dto.IpInfo;
import com.ipaccessanalyzer.domain.model.analysisReport.ReportStatistic;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.BaseSummary;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.RatioSummary;

public record AnalysisResultStatisticResponse(
	long totalCount,
	List<IpAccessResponse> topPaths,
	List<StatusCodeStatisticResponse> topStatusCodes,
	List<EnrichedIpCount> topIps
) {

	public static AnalysisResultStatisticResponse from(
		ReportStatistic result,
		List<IpInfo> enrichedIps
	) {
		List<BaseSummary> topIpsCounts = result.getTopIps();

		List<EnrichedIpCount> enriched = IntStream.range(0, topIpsCounts.size())
			.mapToObj(i -> {
				BaseSummary entry = topIpsCounts.get(i);
				IpInfo ipInfo = (i < enrichedIps.size()) ? enrichedIps.get(i) : null;
				return EnrichedIpCount.of(ipInfo, entry.getCount());
			}).toList();

		return new AnalysisResultStatisticResponse(
			result.getTotalCount(),
			convertIpAccessResposne(result.getTopPaths()),
			convertStatusCodeStatisticResponse(result.getStatusCodeStats()),
			enriched
		);
	}

	private static List<IpAccessResponse> convertIpAccessResposne(List<BaseSummary> entries) {
		return entries.stream().map(e -> IpAccessResponse.from(e.getKey(), e.getCount())).toList();
	}

	private static List<StatusCodeStatisticResponse> convertStatusCodeStatisticResponse(List<BaseSummary> codeStatistic) {
    return codeStatistic.stream()
            .filter(RatioSummary.class::isInstance)
            .map(RatioSummary.class::cast)
            .map(e -> StatusCodeStatisticResponse.from(e.getKey(), e.getRatio()))
            .toList();
	}
}
