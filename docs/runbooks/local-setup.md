# Local Setup

## Prerequisites
- Docker Desktop (ต้องรันอยู่)
- Node.js LTS (สำหรับ `apps/web`) — TODO: scaffold ยังไม่เสร็จ
- JDK 21 (สำหรับ `apps/policy-api`) — TODO: scaffold ยังไม่เสร็จ
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
| Adminer (DB web UI) | เปิด http://localhost:8081 | หน้า login ขึ้น — กรอก System: PostgreSQL, Server: `postgres`, Username/Password ตาม `.env`, Database: `vmi` |

## 4. Stop / clean up
```bash
make down       # หยุด container เก็บ volume ไว้
make clean      # หยุดและลบ volume (ข้อมูลหาย)
```

## Next steps (ยังไม่เสร็จ — TODO)
- Scaffold `apps/web` ด้วย Vite: `npm create vite@latest . -- --template react-ts`
- Scaffold `apps/policy-api` ด้วย Spring Initializr (Web, Data JPA, PostgreSQL Driver, Validation, Actuator)
- เชื่อม `policy-api` เข้ากับ postgres/minio/keycloak ที่รันอยู่แล้ว
- ถ้า API ไม่ตอบสนองหลัง start ให้ดู [troubleshoot-api-unhealthy.md](troubleshoot-api-unhealthy.md)
