package com.vmi.policyapi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class สำหรับ integration test ที่ต้องการ full ApplicationContext จริง
 * (PostgreSQL ผ่าน Testcontainers, JwtDecoder แบบ lazy) — extend จากนี้แทนที่จะเขียน
 * @Import(TestcontainersConfiguration.class) + @SpringBootTest ซ้ำทุกไฟล์
 *
 * หมายเหตุ: @DynamicPropertySource ต้องอยู่ในตัว test class เอง (หรือ base class ที่ extend)
 * เท่านั้น — เขียนไว้ใน @Import class เฉยๆ (เช่น TestcontainersConfiguration) แล้ว Spring
 * TestContext จะไม่เห็นและไม่เรียกให้
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public abstract class AbstractIntegrationTest {

	@DynamicPropertySource
	static void testProperties(DynamicPropertyRegistry registry) {
		registry.add(
			"spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
			() -> "http://localhost:8080/realms/vmi/protocol/openid-connect/certs");
		// ไม่มี Redis Testcontainer ใน test context นี้ (แค่ Postgres) — ปิด caching ระหว่าง
		// test เพื่อไม่ให้ @Cacheable พยายามต่อ Redis จริงที่เครื่อง dev (ซึ่งต้อง password
		// ที่ test ไม่รู้) พฤติกรรม caching เองถูก verify แยกด้วยการรันแอปจริงคู่กับ Redis จริง
		registry.add("spring.cache.type", () -> "none");
	}

}
