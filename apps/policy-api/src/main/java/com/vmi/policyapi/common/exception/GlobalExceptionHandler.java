package com.vmi.policyapi.common.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
	}

	// @RestControllerAdvice ทำงานภายใน DispatcherServlet ก่อนที่ ExceptionTranslationFilter
	// ของ Spring Security (อยู่นอก filter chain) จะได้มีโอกาสแปลง AuthorizationDeniedException
	// เป็น 403 เอง — ถ้าไม่จับ exception นี้ไว้ตรงๆ มันจะตกไปโดน @ExceptionHandler(Exception.class)
	// ข้างล่างแทน กลายเป็น 500 ทั้งที่ควรเป็น 403
	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "Access denied", request, null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
			.map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
			.toList();
		return build(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
	}

	// เช่น sort=invalidField ที่ไม่มีอยู่จริงบน entity — เป็นความผิดของ input ฝั่ง client
	// (เดา field ผิด, พิมพ์ผิด) ไม่ใช่ server พัง ต้องเป็น 400 ไม่ใช่ 500 ไม่งั้น frontend
	// จะเข้าใจผิดว่า backend มีปัญหาทั้งที่จริงๆ แค่ query param ที่ส่งมาไม่ถูก
	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	public ResponseEntity<ErrorResponse> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "Invalid request parameters", request, null);
	}

	// สุดท้ายกันไม่ให้ stack trace หลุดออก client — log ฝั่ง server แทน
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception at {}", request.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, null);
	}

	private ResponseEntity<ErrorResponse> build(
		HttpStatus status, String message, HttpServletRequest request, List<ErrorResponse.FieldError> fieldErrors) {
		ErrorResponse body = new ErrorResponse(
			Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fieldErrors);
		return ResponseEntity.status(status).body(body);
	}

}
