package com.vmi.policyapi.common.exception;

import java.time.Instant;
import java.util.List;

// shape ที่ apps/web ใช้ต่อได้ทันที — timestamp/status/error/message/path มาตรฐาน
// + fieldErrors สำหรับ validation error ที่ frontend map ไปโชว์ทีละ field ในฟอร์มได้
public record ErrorResponse(
	Instant timestamp,
	int status,
	String error,
	String message,
	String path,
	List<FieldError> fieldErrors) {

	public record FieldError(String field, String message) {
	}

}
