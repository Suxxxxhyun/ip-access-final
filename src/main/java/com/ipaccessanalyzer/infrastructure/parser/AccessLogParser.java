package com.ipaccessanalyzer.infrastructure.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.ipaccessanalyzer.application.dto.ParsedLog;
import com.ipaccessanalyzer.domain.model.AccessLog;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessLogParser implements LogParser {
	private static final int COLUMN_SIZE = 12;

	private static final CSVFormat FORMAT =
		CSVFormat.DEFAULT.builder()
			.setHeader(AccessLog.headers())
			.setTrim(true)
			.setIgnoreEmptyLines(true)
			.setQuote('"')
			.build();

	private static final DateTimeFormatter TIMESTAMP_FORMATTER =
		DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss.SSS a", Locale.ENGLISH);

	private final PathNormalizer pathNormalizer;

	@Override
	public ParsedLog parse(String line) {
		try (CSVParser parser = CSVParser.parse(line, FORMAT)) {
			CSVRecord record = parser.getRecords().get(0);

			if (record.size() != COLUMN_SIZE) {
				throw new IllegalArgumentException("Invalid column size: " + record.size());
			}

			Instant timestamp = LocalDateTime
				.parse(record.get(AccessLog.TIME_STAMP.getHeader()), TIMESTAMP_FORMATTER)
				.atZone(ZoneId.of("UTC"))
				.toInstant();

			String normalizedPath = pathNormalizer.normalize(record.get(AccessLog.REQUEST_URI.getHeader()));
			int httpStatus = Integer.parseInt(record.get(AccessLog.HTTP_STATUS.getHeader()));
			String clientIp = record.get(AccessLog.CLIENT_IP.getHeader());

			return ParsedLog.of(timestamp, normalizedPath, httpStatus, clientIp);

		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid log line", e);
		}
	}
}
