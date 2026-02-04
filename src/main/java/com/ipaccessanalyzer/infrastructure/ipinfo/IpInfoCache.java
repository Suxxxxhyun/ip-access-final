package com.ipaccessanalyzer.infrastructure.ipinfo;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ipaccessanalyzer.application.dto.IpInfo;

@Component
public class IpInfoCache {

	private final Map<String, Cached> cache = new ConcurrentHashMap<>();
	private static final Duration TTL = Duration.ofMinutes(10);

	public IpInfo get(String ip) {
		Cached cached = cache.get(ip);
		if (cached == null || cached.isExpired()) return null;
		return cached.value();
	}

	public void put(String ip, IpInfo info) {
		cache.put(ip, new Cached(info, Instant.now()));
	}

	private record Cached(IpInfo value, Instant createdAt) {
		boolean isExpired() {
			return createdAt.plus(TTL).isBefore(Instant.now());
		}
	}
}
