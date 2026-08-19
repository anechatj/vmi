package com.vmi.policyapi.master.coveragetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CoverageTypeCreateRequest(
	@NotBlank @Size(max = 50) String code,
	@NotBlank @Size(max = 100) String name,
	@Size(max = 500) String description,
	@NotNull @PositiveOrZero Integer sortOrder) {
}
