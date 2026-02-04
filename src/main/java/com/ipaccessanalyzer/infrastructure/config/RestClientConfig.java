package com.ipaccessanalyzer.infrastructure.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Value("${ipinfo.base-url}")
	private String baseUrl;

	@Bean
	public RestClient restClient() {
		PoolingHttpClientConnectionManager connectionManager =
			PoolingHttpClientConnectionManagerBuilder.create()
				.setMaxConnTotal(50)        // 전체 최대 커넥션 - 분석 조회 트래픽이 몰려도 무제한으로 커넥션이 생성되지 않도록 제한
				.build();

		RequestConfig requestConfig = RequestConfig.custom()
			.setResponseTimeout(Timeout.ofMilliseconds(500)) // 요청 전송 후 응답을 기다리는 최대 시간 - IP 정보는 핵심 데이터가 아니므로 짧게 설정
			.build();

		CloseableHttpClient httpClient = HttpClients.custom()
			.setConnectionManager(connectionManager) // 커넥션 풀 적용
			.setDefaultRequestConfig(requestConfig) // 모든 요청에 기본 타임아웃 설정 적용
			.build();

		HttpComponentsClientHttpRequestFactory requestFactory =
			new HttpComponentsClientHttpRequestFactory(httpClient);

		return RestClient.builder()
			.baseUrl(baseUrl)
			.requestFactory(requestFactory)
			.build();
	}
}
