package com.ipaccessanalyzer.presentation.dto.response;

public record IpAccessResponse(
	String key,
	long count
) {
	public static IpAccessResponse from(String key, long count){
		return new IpAccessResponse(key, count);
	}
}
