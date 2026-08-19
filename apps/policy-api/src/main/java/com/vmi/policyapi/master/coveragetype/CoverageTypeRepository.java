package com.vmi.policyapi.master.coveragetype;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface CoverageTypeRepository extends JpaRepository<CoverageType, UUID> {

	boolean existsByCodeIgnoreCase(String code);

}
