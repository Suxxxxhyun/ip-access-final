package com.ipaccessanalyzer.exception;

import java.util.UUID;

import lombok.Getter;

@Getter
public class AnalysisResultNotFoundException extends RuntimeException {

	private final UUID reportId;

	public AnalysisResultNotFoundException(UUID reportId) {
		super("Analysis result not found. reportId=" + reportId);
		this.reportId = reportId;
	}

}

