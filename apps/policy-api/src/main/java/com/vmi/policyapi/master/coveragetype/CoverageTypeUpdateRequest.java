package com.vmi.policyapi.master.coveragetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// ไม่มี "code" — ตั้งใจ ไม่ให้แก้ business key หลังสร้างแล้ว (ที่อื่นอาจอ้างอิง code นี้อยู่)
// ไม่มี "active" — ปิดผ่าน DELETE endpoint (soft delete) แยกต่างหาก ไม่ปนกับ update ทั่วไป
public record CoverageTypeUpdateRequest(
	@NotBlank @Size(max = 100) String name,
	@Size(max = 500) String description,
	@NotNull @PositiveOrZero Integer sortOrder) {
}
