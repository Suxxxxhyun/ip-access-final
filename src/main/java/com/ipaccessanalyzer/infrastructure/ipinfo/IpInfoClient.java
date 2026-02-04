package com.ipaccessanalyzer.infrastructure.ipinfo;

import com.ipaccessanalyzer.application.dto.IpInfo;

public interface IpInfoClient {
	IpInfo fetch(String ip);
}
