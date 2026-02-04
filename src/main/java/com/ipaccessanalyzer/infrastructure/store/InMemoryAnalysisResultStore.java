package com.ipaccessanalyzer.infrastructure.store;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ipaccessanalyzer.domain.model.analysisReport.ReportStatistic;

@Component
public class InMemoryAnalysisResultStore implements AnalysisResultStore {

	private final ConcurrentHashMap<UUID, ReportStatistic> store = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, UUID> hashIndex = new ConcurrentHashMap<>();

	@Override
	public void save(ReportStatistic result, String fileHash) {
		store.put(result.getReportId(), result);
		hashIndex.put(fileHash, result.getReportId());
	}

	@Override
	public Optional<ReportStatistic> findById(UUID reportId) {
		return Optional.ofNullable(store.get(reportId));
	}

	@Override
	public Optional<ReportStatistic> findByFileHash(String fileHash) {
		return Optional.ofNullable(hashIndex.get(fileHash)).map(store::get);
	}
}
