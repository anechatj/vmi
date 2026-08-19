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
./mvnw test      # unit test เท่านั้น (เร็ว, ไม่ต้องมี Docker)
./mvnw verify    # unit + integration test (*IT.java ผ่าน failsafe, ต้องมี Docker Desktop เปิดอยู่)
```

**สำคัญ**: ไฟล์ที่ลงท้าย `IT` (เช่น `CoverageTypeControllerIT`) คือ integration test ที่รันผ่าน **failsafe plugin** — `mvn test` (surefire) **ไม่รันให้** ต้องใช้ `mvn verify` เท่านั้น แยกไว้เพื่อให้ unit test รันเร็วบ่อยๆ ระหว่าง dev โดยไม่ต้องรอ Testcontainers ทุกครั้ง

## จุดที่ควรรู้ (เจอจริงตอน setup)

- **`spring.jpa.open-in-view`** ปิดไว้ตั้งแต่แรก (`application.yaml`) — ค่า default ของ Spring คือเปิด ซึ่งซ่อนปัญหา N+1 ได้ง่าย
- **`jwk-set-uri` แทน `issuer-uri`** สำหรับ OAuth2 Resource Server — `issuer-uri` ทำให้ Spring เรียก `/.well-known/openid-configuration` แบบ synchronous ตอน bean startup ถ้า Keycloak ไม่พร้อมพอดีตอนนั้น app จะ start ไม่ขึ้นเลย ส่วน `jwk-set-uri` ดึง key แบบ lazy ตอน validate token จริงเท่านั้น
- **`@DynamicPropertySource` ต้องอยู่ในตัว test class หรือ enclosing class เท่านั้น** — เขียนไว้ใน `@Import`ed class เฉยๆ (เช่น `TestcontainersConfiguration`) แล้ว Spring TestContext จะไม่เห็นและไม่เรียก ต้องใช้ `AbstractIntegrationTest` (base class) แทน
- **`src/test/resources/application.yaml` จะ shadow ทับ `src/main/resources/application.yaml` ทั้งไฟล์** ไม่ใช่ merge — ถ้าจะเพิ่ม property สำหรับ test ให้ใช้ `@DynamicPropertySource` แทนสร้างไฟล์ซ้ำชื่อ
- **`*IT.java` ต้องมี failsafe plugin ถึงจะรัน** — ตั้งชื่อ integration test ลงท้าย `IT` ได้ แต่ถ้า `pom.xml` ไม่มี `maven-failsafe-plugin` มันจะไม่รันเงียบๆ ทั้ง `mvn test` และไม่ error ด้วย (เพราะ surefire ไม่ match ชื่อไฟล์นี้อยู่แล้ว)
- **Spring Boot 4 ย้ายไปใช้ Jackson 3 (`tools.jackson`) แล้ว** — `com.fasterxml.jackson.databind.ObjectMapper` (Jackson 2) ยังอยู่ใน classpath (transitive) แต่ **ไม่มี Spring bean ให้ `@Autowired` อีกต่อไป** ใน test ที่ต้อง serialize JSON ให้ `new ObjectMapper()` เอง
- **`@RestControllerAdvice` แบบ catch-all `Exception.class` จะจับ `AuthorizationDeniedException` ของ Spring Security ไปด้วย** — กลายเป็น 500 แทนที่จะเป็น 403 ที่ Spring Security ควรแปลงให้เอง ต้องเขียน `@ExceptionHandler(AuthorizationDeniedException.class)` แยกไว้ก่อนตัว catch-all เสมอ
- **ไม่มี Redis Testcontainer** — `@Cacheable`/`@CacheEvict` ใน integration test ปิดด้วย `spring.cache.type: none` (ผ่าน `@DynamicPropertySource`) เพราะ Redis จริงบนเครื่อง dev ต้องใช้ password ที่ test ไม่รู้ — caching ถูก verify แยกด้วยการรันแอปจริงคู่กับ Redis จริงแล้วเช็ค `redis-cli KEYS`

## Feature ที่มีแล้ว

**`master/coveragetype`** — CRUD ประเภทความคุ้มครอง (`/api/v1/master/coverage-types`) ตัวอย่างแรกของ vertical slice เต็มรูปแบบ: entity + Flyway migration + DTO/MapStruct mapper + service (cache ผ่าน Redis, optimistic locking ผ่าน `@Version`) + controller (pagination, RBAC ผ่าน `@PreAuthorize`) + global exception handling + unit test (Mockito) + integration test (MockMvc + Testcontainers) — ใช้เป็น template สำหรับ feature ถัดไปได้เลย

## API docs

`http://localhost:8081/v3/api-docs` (ต้องรันแอปก่อน) — ใช้ generate TypeScript client ผ่าน `scripts/generate-api-client.sh` ที่ root ของ repo
