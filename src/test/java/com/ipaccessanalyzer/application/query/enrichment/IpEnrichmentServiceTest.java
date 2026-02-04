package com.ipaccessanalyzer.application.query.enrichment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClientException;

import com.ipaccessanalyzer.application.dto.IpInfo;
import com.ipaccessanalyzer.application.service.IpEnrichmentService;
import com.ipaccessanalyzer.infrastructure.ipinfo.IpInfoCache;
import com.ipaccessanalyzer.infrastructure.ipinfo.IpInfoClient;

@SpringBootTest(properties = "ipinfo.token=test-token")
class IpEnrichmentServiceTest {

	@Autowired
	private IpEnrichmentService ipEnrichmentService;

	@Autowired
	private IpInfoClient ipInfoClient; 

	@TestConfiguration
	static class TestConfig {

		@Bean
		public IpInfoClient ipInfoClient() {
			IpInfoClient mockClient = mock(IpInfoClient.class);
			when(mockClient.fetch(anyString()))
				.thenThrow(new RestClientException("fail"));
			return mockClient;
		}

		@Bean
		public IpInfoCache ipInfoCache() {
			return new IpInfoCache();
		}

		@Bean
		public IpEnrichmentService ipEnrichmentService(IpInfoClient ipInfoClient, IpInfoCache cache) {
			return new IpEnrichmentService(ipInfoClient, cache);
		}
	}

	@Test
	void testRetryAndFallback() {
		String testIp = "8.8.8.8";

		IpInfo result = ipEnrichmentService.getIpInfo(testIp);

		assertEquals(testIp, result.getIp());
		assertEquals("UNKNOWN", result.getCountry());

		verify(ipInfoClient, times(2)).fetch(testIp);
	}
}


