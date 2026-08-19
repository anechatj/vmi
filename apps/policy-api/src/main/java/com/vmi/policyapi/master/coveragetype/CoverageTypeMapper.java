package com.vmi.policyapi.master.coveragetype;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface CoverageTypeMapper {

	CoverageTypeResponse toResponse(CoverageType entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", constant = "true")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	CoverageType toEntity(CoverageTypeCreateRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "code", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	void updateEntityFromRequest(CoverageTypeUpdateRequest request, @MappingTarget CoverageType entity);

}
