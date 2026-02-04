package com.ipaccessanalyzer.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrichedIpCount {
	private final IpInfo ipInfo; // Enriched IP 정보
	private final long count;    // 요청 수

	public static EnrichedIpCount of(IpInfo ipInfo, long count){
		return new EnrichedIpCount(ipInfo, count);
	}
}
