package com.ipaccessanalyzer.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ipaccessanalyzer.application.dto.IpInfo;
import com.ipaccessanalyzer.application.usecase.AnalysisQueryUseCase;
import com.ipaccessanalyzer.domain.model.analysisReport.ReportStatistic;
import com.ipaccessanalyzer.exception.AnalysisResultNotFoundException;
import com.ipaccessanalyzer.infrastructure.store.AnalysisResultStore;
import com.ipaccessanalyzer.presentation.dto.response.AnalysisResultStatisticResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisQueryService implements AnalysisQueryUseCase {

	private final AnalysisResultStore resultStore;
	private final IpEnrichmentService ipEnrichmentService;

	@Override
	public AnalysisResultStatisticResponse getResult(UUID reportId) {
		ReportStatistic result = resultStore.findById(reportId)
			.orElseThrow(() -> new AnalysisResultNotFoundException(reportId));

		List<String> topIpKeys = result.getTopIps()
			.stream()
			.map(item -> item.getKey())
			.toList();

		// Enriched IP 조회
		List<IpInfo> enrichedIps = ipEnrichmentService.getIpInfos(topIpKeys);

		// Response 변환
		return AnalysisResultStatisticResponse.from(result, enrichedIps);
	}
}
