package com.vmi.policyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

// ประกาศ security scheme ไว้ที่เดียว ให้ controller อื่นอ้างอิงชื่อ "bearerAuth" ได้ทั่วโปรเจกต์
// ผ่าน @SecurityRequirement(name = "bearerAuth") — ไม่มีตัวนี้ Swagger UI จะไม่มีปุ่ม Authorize เลย
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT")
@SpringBootApplication
public class PolicyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyApiApplication.class, args);
	}

}
