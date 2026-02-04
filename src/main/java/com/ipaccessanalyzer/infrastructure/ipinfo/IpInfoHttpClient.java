package com.ipaccessanalyzer.infrastructure.ipinfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.ipaccessanalyzer.application.dto.IpInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpInfoHttpClient implements IpInfoClient {

	private final RestClient restClient;
	@Value("${ipinfo.token}")
	private String token;

	@Retryable(
			retryFor = { RestClientException.class },
			maxAttempts = 2,
			backoff = @Backoff(delay = 500, multiplier = 2)
	)
	@Override
	public IpInfo fetch(String ip) {
		try {
			IpInfoResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/{ip}")
					.queryParam("token", token)
					.build(ip))
				.retrieve()
				.body(IpInfoResponse.class);
			return response != null ? response.toDomain() : IpInfo.unknown(ip);
		} catch (RestClientException e) {
			log.warn("IpInfo fetch failed for {}: {}", ip, e.getMessage());
			return IpInfo.unknown(ip);
		}
	}
}
