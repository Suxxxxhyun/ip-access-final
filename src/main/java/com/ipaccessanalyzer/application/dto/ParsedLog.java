package com.ipaccessanalyzer.application.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParsedLog{
	private final Instant timestamp;
	private final String path;
	private final int statusCode;
	private final String clientIp;

	public static ParsedLog of(Instant timestamp, String path, int statusCode, String clientIp){
		return new ParsedLog(timestamp, path, statusCode, clientIp);
	}
}


