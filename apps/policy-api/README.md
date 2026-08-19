# apps/policy-api

Spring Boot 4.1 (Java 21) service สำหรับกรมธรรม์ภาคสมัครใจ (Modular Monolith — ดู [ADR-0001](../../docs/adr/0001-use-modular-monolith.md))

## Stack

| ส่วน | ที่ใช้ |
|---|---|
| Web | Spring Web (MVC) |
| Data | Spring Data JPA + PostgreSQL + Flyway (schema มาจาก migration เท่านั้น, `ddl-auto: validate`) |
| Security | Spring Security + OAuth2 Resource Server ต่อกับ Keycloak realm `vmi` (JWT, RBAC ผ่าน `realm_access.roles`) |
| Cache | Spring Data Redis + Cache Abstraction |
| Validation | Jakarta Bean Validation |
| API docs | springdoc-openapi (`/v3/api-docs`) — `packages/api-client` generate TS client จากตรงนี้ |
| Mapper | MapStruct (DTO ↔ Entity) |
| Ops | Spring Boot Actuator |
| Test | JUnit5 + Mockito + Testcontainers (PostgreSQL จริงใน container ตอน integration test) |

## โครงสร้าง package (ตาม ADR-0001)

```
com.vmi.policyapi/
├─ master/     # ข้อมูลอ้างอิง — ประเภทความคุ้มครอง, จังหวัด, ตารางเบี้ย ฯลฯ
├─ policy/     # core domain — กรมธรรม์, ผู้เอาประกัน, สลักหลัง, ต่ออายุ
├─ document/   # ผูกกับ MinIO ตาม ADR-0004
├─ payment/    # การชำระเงิน
├─ reporting/  # read-only, query ข้าม package อื่น
└─ common/     # shared: SecurityConfig, GlobalExceptionHandler, base config
```

**กฎ**: แต่ละ package ห้าม import internal class ของ package อื่นตรงๆ ต้องคุยผ่าน public service/interface เท่านั้น — นี่คือสิ่งที่ทำให้แตกเป็น microservice ทีหลังได้โดยไม่ต้อง rewrite

## รันแบบ local

```bash
# ต้องมี infra ครบก่อน (postgres, redis, keycloak) — ดู docs/runbooks/local-setup.md
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

รันที่ `http://localhost:8081` — เช็คว่าเชื่อม Postgres/Redis ได้จริงที่ `http://localhost:8081/actuator/health`

## รัน test

```bash
./mvnw test
```

Integration test (`PolicyApiApplicationTests` และตัวที่ extend `AbstractIntegrationTest`) รัน PostgreSQL จริงผ่าน Testcontainers ไม่ต้องมี infra รันอยู่ก่อน — ต้องมี Docker Desktop เปิดอยู่เท่านั้น

## จุดที่ควรรู้ (เจอจริงตอน setup)

- **`spring.jpa.open-in-view`** ปิดไว้ตั้งแต่แรก (`application.yaml`) — ค่า default ของ Spring คือเปิด ซึ่งซ่อนปัญหา N+1 ได้ง่าย
- **`jwk-set-uri` แทน `issuer-uri`** สำหรับ OAuth2 Resource Server — `issuer-uri` ทำให้ Spring เรียก `/.well-known/openid-configuration` แบบ synchronous ตอน bean startup ถ้า Keycloak ไม่พร้อมพอดีตอนนั้น app จะ start ไม่ขึ้นเลย ส่วน `jwk-set-uri` ดึง key แบบ lazy ตอน validate token จริงเท่านั้น
- **`@DynamicPropertySource` ต้องอยู่ในตัว test class หรือ enclosing class เท่านั้น** — เขียนไว้ใน `@Import`ed class เฉยๆ (เช่น `TestcontainersConfiguration`) แล้ว Spring TestContext จะไม่เห็นและไม่เรียก ต้องใช้ `AbstractIntegrationTest` (base class) แทน
- **`src/test/resources/application.yaml` จะ shadow ทับ `src/main/resources/application.yaml` ทั้งไฟล์** ไม่ใช่ merge — ถ้าจะเพิ่ม property สำหรับ test ให้ใช้ `@DynamicPropertySource` แทนสร้างไฟล์ซ้ำชื่อ

## API docs

`http://localhost:8081/v3/api-docs` (ต้องรันแอปก่อน) — ใช้ generate TypeScript client ผ่าน `scripts/generate-api-client.sh` ที่ root ของ repo
