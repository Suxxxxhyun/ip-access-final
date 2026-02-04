package com.ipaccessanalyzer.domain.model.analysisReport;

import java.util.List;
import java.util.UUID;

import com.ipaccessanalyzer.domain.model.analysisReport.summary.BaseSummary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName="of")
public class ReportStatistic {
	private final UUID reportId;
	private final long totalCount;
	private final List<BaseSummary> topPaths;
	private final List<BaseSummary> statusCodeStats;
	private final List<BaseSummary> topIps;

	public List<String> getTopIpKeys() {
		return topIps.stream().map(BaseSummary::getKey).toList();
	}
}

