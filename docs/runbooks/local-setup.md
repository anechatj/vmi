# Local Setup

## Prerequisites
- Docker Desktop (ต้องรันอยู่)
- Node.js LTS (สำหรับ `apps/web`)
- JDK 21 (สำหรับ `apps/policy-api`)
- (Optional) `make` — ถ้าไม่มีบน Windows ใช้คำสั่ง `docker compose` ตรงแทนได้ (ดูด้านล่าง)

## 1. Copy environment file
คัดลอกค่า default (**อย่า commit `.env` จริงเข้า git**):

```bash
cp infra/docker/.env.example infra/docker/.env
```

## 2. Start infra services (Postgres, MinIO, Keycloak)

ใช้ Makefile:
```bash
make up
```

หรือรันตรงถ้าไม่มี `make`:
```bash
docker compose -f infra/docker/docker-compose.local.yml --env-file infra/docker/.env up -d
```

## 3. Verify each service

| Service | วิธีเช็ค | ผลที่คาดหวัง |
|---|---|---|
| PostgreSQL | `docker compose -f infra/docker/docker-compose.local.yml ps postgres` | STATUS = healthy — เชื่อมจากเครื่อง host ที่ `localhost:5433` (ไม่ใช่ 5432 เพื่อเลี่ยงชนกับ native PostgreSQL ที่อาจติดตั้งไว้อยู่แล้ว) |
| MinIO Console | เปิด http://localhost:9001 | login ด้วยค่าใน `.env` ได้ |
| MinIO bucket | ดู log ของ `vmi-minio-init`: `docker compose -f infra/docker/docker-compose.local.yml logs minio-init` | เห็นข้อความ "MinIO bucket setup complete" |
| Keycloak | เปิด http://localhost:8080 | หน้า admin console ขึ้น |
| Adminer (DB web UI) | เปิด http://localhost:8082 | หน้า login ขึ้น — กรอก System: PostgreSQL, Server: `postgres`, Username/Password ตาม `.env`, Database: `vmi` |
| Redis | `docker compose -f infra/docker/docker-compose.local.yml ps redis` | STATUS = healthy |

## 4. รัน apps ตัวจริง

```bash
# Frontend — http://localhost:5173
npm --prefix apps/web run dev

# Backend — http://localhost:8081 (ต้องมี infra ข้างบนรันอยู่ก่อน)
cd apps/policy-api && SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

เช็คว่า `policy-api` เชื่อม Postgres/Redis จริงได้ที่ `http://localhost:8081/actuator/health` — ควรเห็น `"db":{"status":"UP"}` และ `"redis":{"status":"UP"}`

## 5. Stop / clean up
```bash
make down       # หยุด container เก็บ volume ไว้
make clean      # หยุดและลบ volume (ข้อมูลหาย)
```

## Next steps (ยังไม่เสร็จ — TODO)
- เขียน entity/migration แรกใน `apps/policy-api` (ยังไม่มี table ใดๆ เลยตอนนี้ — Flyway validate ผ่านเพราะ schema ว่างเปล่า)
- ต่อ `apps/web` เข้ากับ `apps/policy-api` จริงผ่าน typed client (`packages/api-client`) แทน placeholder fetch ปัจจุบัน
- ถ้า API ไม่ตอบสนองหลัง start ให้ดู [troubleshoot-api-unhealthy.md](troubleshoot-api-unhealthy.md)
