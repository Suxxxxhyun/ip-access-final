package com.ipaccessanalyzer.infrastructure.store;

import java.util.Optional;
import java.util.UUID;

import com.ipaccessanalyzer.domain.model.analysisReport.ReportStatistic;

public interface AnalysisResultStore {
	void save(ReportStatistic result, String fileHash);
	Optional<ReportStatistic> findById(UUID reportId);
	Optional<ReportStatistic> findByFileHash(String fileHash);
}
