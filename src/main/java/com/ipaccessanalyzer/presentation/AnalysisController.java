package com.ipaccessanalyzer.presentation;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipaccessanalyzer.application.usecase.AnalysisLogUseCase;
import com.ipaccessanalyzer.application.usecase.AnalysisQueryUseCase;
import com.ipaccessanalyzer.presentation.dto.request.AnalysisRequest;
import com.ipaccessanalyzer.presentation.dto.response.AnalysisResultResponse;
import com.ipaccessanalyzer.presentation.dto.response.AnalysisResultStatisticResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
	private final AnalysisLogUseCase analysisLogUseCase;
	private final AnalysisQueryUseCase analysisQueryUseCase;


	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<AnalysisResultResponse> analyze(
		@Validated @ModelAttribute AnalysisRequest request) throws IOException {
		log.info("analysis.request filename={} size={}", request.file().getOriginalFilename(), request.file().getSize());

		AnalysisResultResponse response = analysisLogUseCase.getOrAnalyze(request.file());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{reportId}")
	public ResponseEntity<AnalysisResultStatisticResponse> getAnalysisResult(
		@PathVariable UUID reportId
	) {
		AnalysisResultStatisticResponse response = analysisQueryUseCase.getResult(reportId);
		return ResponseEntity.ok(response);
	}
}

