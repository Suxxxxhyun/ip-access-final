package com.ipaccessanalyzer.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ipaccessanalyzer.application.dto.ParsedLog;
import com.ipaccessanalyzer.application.usecase.AnalysisLogUseCase;
import com.ipaccessanalyzer.domain.enums.FieldSpec;
import com.ipaccessanalyzer.domain.model.analysisReport.ReportStatistic;
import com.ipaccessanalyzer.domain.model.analysisReport.item.ReportItem;
import com.ipaccessanalyzer.domain.model.analysisReport.item.SamplingReportItem;
import com.ipaccessanalyzer.domain.model.analysisReport.summary.BaseSummary;
import com.ipaccessanalyzer.infrastructure.parser.LogParser;
import com.ipaccessanalyzer.infrastructure.store.AnalysisResultStore;
import com.ipaccessanalyzer.infrastructure.store.hash.FileHashGenerator;
import com.ipaccessanalyzer.presentation.dto.response.AnalysisResultResponse;
import com.ipaccessanalyzer.support.ReportContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisLogService implements AnalysisLogUseCase {

	private static final int TOP_N = 5;

	private final LogParser logParser;
	private final AnalysisResultStore resultStore;
	private final FileHashGenerator fileHashGenerator;

	@Override
	public AnalysisResultResponse getOrAnalyze(MultipartFile file) throws IOException {

		String fileHash = fileHashGenerator.sha256(file);
		return resultStore.findByFileHash(fileHash)
        .map(AnalysisResultResponse::from)
        .orElseGet(() -> {
            try {
                return analyze(file, fileHash);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
	}

	@Override
	public AnalysisResultResponse analyze(MultipartFile file, String fileHash) throws IOException {
		final UUID reportId = UUID.randomUUID();
		log.info("analysis.start reportId={}", reportId);

		final ReportContext context = new ReportContext(reportId);
		final ReportStatistic result = this.parser(file, context);
		final SamplingReportItem errorTrace = context.getErrorTrace();

		resultStore.save(result, fileHash);

		log.info("analysis.end reportId={}", reportId);

		return AnalysisResultResponse.from(
				result,
				AnalysisResultResponse.ErrorResponse.of(context.getErrorCount(), errorTrace.getSamples()
			)
		);
	}

	private ReportStatistic parser(MultipartFile file, ReportContext context) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

			reader.readLine();

			String line;
			while ((line = reader.readLine()) != null) {

				try {
					final ParsedLog parsed = logParser.parse(line);
					context.accumulate(parsed);
				} catch (RuntimeException e) {
					context.recordParseError(line);
					log.debug("analysis.parse.fail id={} line={}", context.getReportId(), line);
				}
			}

			return this.toReportStatistic(context.getReportId(), context, TOP_N);
		}
	}

	/**
	 * 최종 통계 객체로 변환
	 */
	private ReportStatistic toReportStatistic(final UUID reportId, final ReportContext statistic, final int topN) {
			final Map<FieldSpec, ReportItem> metrics = statistic.getMetrics();
			final long totalCount = statistic.getTotalCount();
			final List<BaseSummary> topPaths = metrics.get(FieldSpec.PATH).getTopRankings(topN);
			final List<BaseSummary> topIps = metrics.get(FieldSpec.IP).getTopRankings(topN);
			final List<BaseSummary> statusCodeStats = metrics.get(FieldSpec.STATUS).getAllByRatio(totalCount);

			return ReportStatistic.of(
					reportId,
					totalCount, 
					topPaths, 
					statusCodeStats, 
					topIps
			);
	}
}
