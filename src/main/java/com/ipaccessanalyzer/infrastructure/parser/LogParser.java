package com.ipaccessanalyzer.infrastructure.parser;

import com.ipaccessanalyzer.application.dto.ParsedLog;

public interface LogParser {
	ParsedLog parse(String line);
}
