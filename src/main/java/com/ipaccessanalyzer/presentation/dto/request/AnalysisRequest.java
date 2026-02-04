package com.ipaccessanalyzer.presentation.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.ipaccessanalyzer.presentation.validation.NotEmptyFile;

public record AnalysisRequest(
	@NotEmptyFile MultipartFile file
) {
}
