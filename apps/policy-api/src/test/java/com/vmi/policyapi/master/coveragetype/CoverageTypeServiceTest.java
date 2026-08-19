package com.vmi.policyapi.master.coveragetype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vmi.policyapi.common.exception.DuplicateResourceException;
import com.vmi.policyapi.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CoverageTypeServiceTest {

	@Mock
	private CoverageTypeRepository repository;

	private CoverageTypeService service;

	@BeforeEach
	void setUp() {
		// ใช้ MapStruct impl จริง (ไม่ mock) เพราะ mapper เป็นแค่ pure conversion ไม่มี
		// dependency ภายนอก — mock มันไปก็ไม่ได้ทดสอบ logic การ mapping จริง
		service = new CoverageTypeService(repository, new CoverageTypeMapperImpl());
	}

	@Test
	void get_returnsMappedResponse_whenFound() {
		CoverageType entity = sampleEntity();
		when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

		CoverageTypeResponse result = service.get(entity.getId());

		assertEquals("CLASS_1", result.code());
		assertEquals("ชั้น 1", result.name());
	}

	@Test
	void get_throwsResourceNotFound_whenMissing() {
		UUID id = UUID.randomUUID();
		when(repository.findById(id)).thenReturn(Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.get(id));
		assertTrue(ex.getMessage().contains(id.toString()));
	}

	@Test
	void create_throwsDuplicate_whenCodeExists() {
		CoverageTypeCreateRequest request = new CoverageTypeCreateRequest("CLASS_1", "ชั้น 1", null, 1);
		when(repository.existsByCodeIgnoreCase("CLASS_1")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> service.create(request));
		verify(repository, never()).save(any());
	}

	@Test
	void create_savesAndReturnsResponse_whenCodeIsNew() {
		CoverageTypeCreateRequest request =
			new CoverageTypeCreateRequest("CLASS_2_PLUS", "ชั้น 2+", "คุ้มครองเพิ่มเติม", 2);
		when(repository.existsByCodeIgnoreCase("CLASS_2_PLUS")).thenReturn(false);
		when(repository.save(any(CoverageType.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CoverageTypeResponse result = service.create(request);

		assertEquals("CLASS_2_PLUS", result.code());
		assertTrue(result.active());
	}

	@Test
	void deactivate_setsActiveFalse() {
		CoverageType entity = sampleEntity();
		when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

		service.deactivate(entity.getId());

		assertFalse(entity.isActive());
	}

	private CoverageType sampleEntity() {
		CoverageType entity = new CoverageType();
		entity.setId(UUID.randomUUID());
		entity.setCode("CLASS_1");
		entity.setName("ชั้น 1");
		entity.setSortOrder(1);
		entity.setActive(true);
		entity.setCreatedAt(Instant.now());
		entity.setUpdatedAt(Instant.now());
		entity.setVersion(0L);
		return entity;
	}

}
