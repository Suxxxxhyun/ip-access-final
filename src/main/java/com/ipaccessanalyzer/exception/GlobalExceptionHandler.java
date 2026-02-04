package com.ipaccessanalyzer.exception;

import java.util.Optional;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
		log.warn("bad request: {}", e.getMessage());
		return toResponse(ErrorCode.INVALID_REQUEST, e.getMessage());
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException e) {
		log.warn("파일 크기 초과: {}", e.getMessage());
		return toResponse(ErrorCode.INVALID_REQUEST, "업로드 가능한 최대 파일 크기를 초과했습니다.");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
		MethodArgumentTypeMismatchException e
	) {
		log.warn("Invalid path variable type: {} ", e.getMessage());

		return toResponse(ErrorCode.INVALID_REQUEST, "UUID형식이 아닙니다.");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException e) {

		String message = e.getBindingResult().getFieldErrors().stream()
			.filter(error -> "file".equals(error.getField()))
			.map(error -> {
				if (error.contains(TypeMismatchException.class)) {
					return "파일 형식이 올바르지 않습니다. 파일을 첨부해주세요.";
				}
				return Optional.ofNullable(error.getDefaultMessage()).orElse("잘못된 요청입니다.");
			}).findFirst()
			.orElse("잘못된 요청입니다.");

		log.warn("validation failed: {}", message);
		return toResponse(ErrorCode.INVALID_REQUEST, message);
	}

	@ExceptionHandler(AnalysisResultNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleAnalysisResultNotFound(
		AnalysisResultNotFoundException e
	) {
		log.info("analysis result not found: {}", e.getMessage());
		return toResponse(ErrorCode.ANALYSIS_RESULT_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
		log.error("unexpected error", e);
		return toResponse(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage());
	}

	private ResponseEntity<ApiErrorResponse> toResponse(ErrorCode errorCode, String customMessage) {
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ApiErrorResponse.builder()
				.errorCode(errorCode.getCode())
				.message(errorCode.getMessage())
				.build());
	}

	private ResponseEntity<ApiErrorResponse> toResponse(ErrorCode errorCode) {
		return toResponse(errorCode, errorCode.getMessage());
	}

}


