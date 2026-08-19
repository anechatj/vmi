package com.vmi.policyapi.master.coveragetype;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vmi.policyapi.common.exception.DuplicateResourceException;
import com.vmi.policyapi.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoverageTypeService {

	private static final String CACHE_NAME = "coverageTypes";

	private final CoverageTypeRepository repository;
	private final CoverageTypeMapper mapper;

	public Page<CoverageTypeResponse> list(Pageable pageable) {
		return repository.findAll(pageable).map(mapper::toResponse);
	}

	// cache เฉพาะ lookup ทีละตัว ไม่ cache หน้า list เพราะ Page<T> serialize เป็น JSON กลับมา
	// เป็น interface ที่ deserialize ตรงๆ ยาก และ pageable ที่ต่างกันแทบไม่ hit cache ซ้ำอยู่แล้ว
	@Cacheable(value = CACHE_NAME, key = "#id")
	public CoverageTypeResponse get(UUID id) {
		return mapper.toResponse(findEntityOrThrow(id));
	}

	@Transactional
	public CoverageTypeResponse create(CoverageTypeCreateRequest request) {
		if (repository.existsByCodeIgnoreCase(request.code())) {
			throw new DuplicateResourceException("Coverage type code already exists: " + request.code());
		}
		CoverageType entity = mapper.toEntity(request);
		return mapper.toResponse(repository.save(entity));
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, key = "#id")
	public CoverageTypeResponse update(UUID id, CoverageTypeUpdateRequest request) {
		CoverageType entity = findEntityOrThrow(id);
		mapper.updateEntityFromRequest(request, entity);
		// ไม่ต้องเรียก repository.save() — entity นี้ managed อยู่ใน persistence context แล้ว
		// (findEntityOrThrow ดึงมาในเดียวกัน @Transactional) Hibernate dirty-check ให้เองตอน commit
		return mapper.toResponse(entity);
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, key = "#id")
	public void deactivate(UUID id) {
		CoverageType entity = findEntityOrThrow(id);
		entity.setActive(false);
	}

	private CoverageType findEntityOrThrow(UUID id) {
		return repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Coverage type not found: " + id));
	}

}
