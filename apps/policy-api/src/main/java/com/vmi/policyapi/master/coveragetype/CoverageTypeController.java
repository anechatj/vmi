package com.vmi.policyapi.master.coveragetype;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/master/coverage-types")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CoverageTypeController {

	private final CoverageTypeService service;

	// เปิดให้ authenticated user ทุกคนอ่านได้ (ผูกกับ .anyRequest().authenticated() ใน
	// SecurityConfig อยู่แล้ว) — เจ้าหน้าที่บันทึกกรมธรรม์ต้อง lookup ค่านี้บ่อยตอนกรอกฟอร์ม
	@GetMapping
	public Page<CoverageTypeResponse> list(@PageableDefault(size = 20, sort = "sortOrder") Pageable pageable) {
		return service.list(pageable);
	}

	@GetMapping("/{id}")
	public CoverageTypeResponse get(@PathVariable UUID id) {
		return service.get(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<CoverageTypeResponse> create(@Valid @RequestBody CoverageTypeCreateRequest request) {
		CoverageTypeResponse created = service.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.id())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('admin')")
	public CoverageTypeResponse update(@PathVariable UUID id, @Valid @RequestBody CoverageTypeUpdateRequest request) {
		return service.update(id, request);
	}

	// soft delete — ปิด active flag ไม่ลบแถวจริง เพราะกรมธรรม์เก่าอาจอ้างอิง coverage type นี้อยู่
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
		service.deactivate(id);
		return ResponseEntity.noContent().build();
	}

}
