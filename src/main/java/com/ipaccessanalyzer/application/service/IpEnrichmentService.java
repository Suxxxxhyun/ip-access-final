package com.ipaccessanalyzer.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ipaccessanalyzer.application.dto.IpInfo;
import com.ipaccessanalyzer.infrastructure.ipinfo.IpInfoCache;
import com.ipaccessanalyzer.infrastructure.ipinfo.IpInfoClient;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpEnrichmentService {
	private final IpInfoClient ipInfoClient;
	private final IpInfoCache cache;

	@Retry(name = "ipinfoRetry", fallbackMethod = "fallback")
	public IpInfo getIpInfo(String ip) {
		IpInfo cached = cache.get(ip);
		if (cached != null) return cached;

		IpInfo info = ipInfoClient.fetch(ip);
		cache.put(ip, info);
		return info;
	}

	public List<IpInfo> getIpInfos(List<String> ips) {
		return ips.stream()
			.map(this::getIpInfo)
			.toList();
	}

	public IpInfo fallback(String ip, Throwable t) {
		return IpInfo.unknown(ip);
	}
}
