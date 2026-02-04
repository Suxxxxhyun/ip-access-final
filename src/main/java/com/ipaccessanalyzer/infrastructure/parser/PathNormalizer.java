package com.ipaccessanalyzer.infrastructure.parser;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * 요청 URI를 통계 집계에 적합한 형태로 정규화하는 컴포넌트.
 *
 * 전체 흐름 예시:
 *
 * 입력 URI:
 *   /api/users/123/profile?verbose=true
 *
 * normalize()
 *   1) stripQuery()
 *      -> /api/users/123/profile
 *
 *   2) path split("/")
 *      -> ["api", "users", "123", "profile"]
 *
 *   3) normalizeSegment()
 *      -> ["api", "users", "{id}", "profile"]
 *
 *   4) depth 제한 (MAX_DEPTH = 5)
 *      -> 그대로 유지
 *
 * 최종 결과:
 *   /api/users/{id}/profile
 */

@Component
public class PathNormalizer {

	private static final Pattern NUMBER = Pattern.compile("^\\d+$");
	private static final Pattern UUID =
		Pattern.compile("^[0-9a-fA-F\\-]{36}$");

	private static final int MAX_DEPTH = 5;


	/**
	 * 원본 요청 URI를 통계 집계용 path로 정규화한다.
	 *
	 * 예시 1:
	 *   입력:  /search?q=abc
	 *   결과:  /search
	 *
	 * 예시 2:
	 *   입력:  /orders/550e8400-e29b-41d4-a716-446655440000/items
	 *   결과:  /orders/{uuid}/items
	 *
	 * 예시 3:
	 *   입력:  /api/users/1/friends/2/photos/3
	 *   처리:
	 *     - depth 6 → MAX_DEPTH 5 적용
	 *   결과:
	 *     /api/users/{id}/friends/{id}
	 *
	 * @param uri 원본 요청 URI
	 * @return 정규화된 path (항상 "/"로 시작)
	 */

	public String normalize(String uri) {
		if (uri == null || uri.isBlank()) {
			return "/";
		}

		String path = stripQuery(uri);

		String normalized = Arrays.stream(path.split("/"))
			.filter(s -> !s.isBlank())
			.map(this::normalizeSegment)
			.limit(MAX_DEPTH)
			.collect(Collectors.joining("/"));

		return "/" + normalized;
	}

	/**
	 * URI에서 쿼리 스트링을 제거한다.
	 *
	 * 처리 예:
	 *   "/api/users/123?active=true"
	 *      -> "/api/users/123"
	 *
	 *   "/health"
	 *      -> "/health" (변경 없음)
	 *
	 * @param uri 원본 URI
	 * @return 쿼리 스트링이 제거된 path
	 */

	private String stripQuery(String uri) {
		int idx = uri.indexOf('?');
		return idx >= 0 ? uri.substring(0, idx) : uri;
	}

	/**
	 * 개별 path segment를 통계 집계에 적합한 형태로 정규화한다.
	 *
	 * 처리 규칙 예:
	 *   "123"  -> "{id}"
	 *   "42"   -> "{id}"
	 *
	 *   "550e8400-e29b-41d4-a716-446655440000"
	 *          -> "{uuid}"
	 *
	 *   "users" -> "users"
	 *
	 * @param segment path의 단일 구간
	 * @return 정규화된 segment
	 */

	private String normalizeSegment(String segment) {
		if (NUMBER.matcher(segment).matches()) {
			return "{id}";
		}
		if (UUID.matcher(segment).matches()) {
			return "{uuid}";
		}
		return segment;
	}
}

