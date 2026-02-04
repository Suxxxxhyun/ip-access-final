package com.ipaccessanalyzer.presentation.dto.response;

public record StatusCodeStatisticResponse(
	String errorCode,
	double ratio
) {
	public static StatusCodeStatisticResponse from(String errorCode, double ratio){
		return new StatusCodeStatisticResponse(errorCode, ratio);
	}
}
