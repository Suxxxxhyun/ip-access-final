package com.ipaccessanalyzer.exception;

import lombok.Builder;

@Builder
public record ApiErrorResponse(
	String errorCode,
	String message
) {}
