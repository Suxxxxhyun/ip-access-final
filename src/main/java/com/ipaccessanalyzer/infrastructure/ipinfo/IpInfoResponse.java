package com.ipaccessanalyzer.infrastructure.ipinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ipaccessanalyzer.application.dto.IpInfo;

import lombok.Getter;

@Getter
public class IpInfoResponse {

	private String ip;
	private String asn;

	@JsonProperty("as_name")
	private String asName;

	@JsonProperty("as_domain")
	private String asDomain;

	@JsonProperty("country_code")
	private String countryCode;

	private String country;

	@JsonProperty("continent_code")
	private String continentCode;

	private String continent;

	public IpInfo toDomain() {
		return IpInfo.builder()
			.ip(ip)
			.asn(asn)
			.asName(asName)
			.asDomain(asDomain)
			.countryCode(countryCode)
			.country(country)
			.continentCode(continentCode)
			.continent(continent)
			.build();
	}
}
