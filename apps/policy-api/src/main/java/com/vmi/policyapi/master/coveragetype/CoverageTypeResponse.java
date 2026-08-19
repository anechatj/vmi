package com.vmi.policyapi.master.coveragetype;

import java.util.UUID;

public record CoverageTypeResponse(
	UUID id,
	String code,
	String name,
	String description,
	Integer sortOrder,
	boolean active) {
}
