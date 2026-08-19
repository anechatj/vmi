package com.vmi.policyapi.master.coveragetype;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coverage_type")
@Getter
@Setter
@NoArgsConstructor
// ไม่ generate equals/hashCode จาก Lombok โดยตั้งใจ — @Data บน JPA entity อันตราย เพราะ
// equals() จาก field ทั้งหมดจะเปลี่ยนค่าไปเรื่อยๆ ตอน entity อยู่ใน Set/Map (Hibernate mutate
// field ระหว่าง lifecycle) ยังไม่มี use case ต้องเทียบ entity ใน collection ตอนนี้ ใช้ default
// identity เพียงพอ — ถ้าต้องการทีหลังค่อยเขียน equals/hashCode จาก id เท่านั้น
public class CoverageType {

	@Id
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "code", nullable = false, unique = true, length = 50)
	private String code;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder;

	@Column(name = "active", nullable = false)
	private boolean active = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = Instant.now();
	}

}
