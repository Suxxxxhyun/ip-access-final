package com.ipaccessanalyzer.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
	SUCCESS_2XX(200, 299, "success"),
	REDIRECT_3XX(300, 399, "redirect"),
	CLIENT_ERROR_4XX(400, 499, "client_error"),
	SERVER_ERROR_5XX(500, 599, "server_error");

	private final int start;
	private final int end;
	private final String name;

	public static StatusCode of(int code) {
		for (StatusCode s : values()) {
			if (code >= s.start && code <= s.end) return s;
		}
		throw new IllegalArgumentException("Unknown status code: " + code);
	}
}
