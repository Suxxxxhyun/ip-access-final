package com.ipaccessanalyzer.application.usecase;

import java.util.UUID;

import com.ipaccessanalyzer.presentation.dto.response.AnalysisResultStatisticResponse;

/**
 *  CQRS패턴 적용 : 분석 결과 조회
 */

public interface AnalysisQueryUseCase {
	AnalysisResultStatisticResponse getResult(UUID reportId);
}
