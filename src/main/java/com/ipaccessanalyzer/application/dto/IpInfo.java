package com.ipaccessanalyzer.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IpInfo {

	private final String ip;
	private final String asn;
	private final String asName;
	private final String asDomain;
	private final String countryCode;
	private final String country;
	private final String continentCode;
	private final String continent;

	public boolean isUnknown() {
		return "UNKNOWN".equals(country);
	}

	public static IpInfo unknown(String ip) {
		return IpInfo.builder()
			.ip(ip)
			.asn("UNKNOWN")
			.asName("UNKNOWN")
			.asDomain("UNKNOWN")
			.countryCode("UNKNOWN")
			.country("UNKNOWN")
			.continentCode("UNKNOWN")
			.continent("UNKNOWN")
			.build();
	}
}
