package com.ipaccessanalyzer.domain.model;

import java.time.Instant;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccessLog {
	TIME_STAMP("TimeGenerated [UTC]"),
	CLIENT_IP("ClientIp"),
	HTTP_METHOD("HttpMethod"),
	REQUEST_URI("RequestUri"),
	USER_AGENT("UserAgent"),
	HTTP_STATUS("HttpStatus"),
	HTTP_VERSION("HttpVersion"),
	RECEIVED_BYTES("ReceivedBytes"),
	SENT_BYTES("SentBytes"),
	CLIENT_RESPONSE_TIME("ClientResponseTime"),
	SSL_PROTOCOL("SslProtocol"),
	ORIGINAL_REQUEST_URI_WITH_ARGS("OriginalRequestUriWithArgs");

	private final String header;

	public static String[] headers() {
		return Arrays.stream(values())
			.map(AccessLog::getHeader)
			.toArray(String[]::new);
	}
}


