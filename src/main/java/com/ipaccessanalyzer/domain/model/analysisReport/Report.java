package com.ipaccessanalyzer.domain.model.analysisReport;

import java.util.List;
import java.util.UUID;

import com.ipaccessanalyzer.domain.model.analysisReport.item.BaseItem;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.BaseSummary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(staticName="of")
public class Report {
	private final UUID id;
	private final List<BaseItem> items;
	private final List<BaseSummary> summaries;
	private final int totalCount;
}
