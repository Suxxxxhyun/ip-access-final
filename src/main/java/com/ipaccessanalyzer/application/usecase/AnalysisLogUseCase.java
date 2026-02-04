package com.ipaccessanalyzer.application.usecase;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.ipaccessanalyzer.presentation.dto.response.AnalysisResultResponse;

public interface AnalysisLogUseCase {
	AnalysisResultResponse getOrAnalyze(MultipartFile file) throws IOException;
	AnalysisResultResponse analyze(MultipartFile file, String fileHash) throws IOException;
}
