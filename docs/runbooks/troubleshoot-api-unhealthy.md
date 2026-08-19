# API unhealthy

## Symptom
`GET /actuator/health` (local: `http://localhost:8081/actuator/health`, production: URL จริง) ตอบ 503 หรือ `{"status":"DOWN"}`

## Likely Causes

| อาการเพิ่มเติมที่เจอ | สาเหตุที่เป็นไปได้ |
|---|---|
| `components.db.status = DOWN` | PostgreSQL ไม่รัน / connection string ผิด / credential ผิด |
| `components.diskSpace.status = DOWN` | Disk เต็ม (มักเจอบน production) |
| Container restart loop ใน `docker compose ps` | Migration fail ตอน startup / exception ตอน bean init |
| Health endpoint ไม่ตอบเลย (connection refused) | Container ไม่ได้รัน หรือ port ไม่ตรง |

## Steps

### Local (Docker Desktop)
1. เช็คสถานะ container: `docker compose -f infra/docker/docker-compose.local.yml ps`
2. ดู log ของ policy-api: `docker compose -f infra/docker/docker-compose.local.yml logs policy-api --tail=200`
3. เช็คว่า postgres healthy: `docker compose -f infra/docker/docker-compose.local.yml ps postgres` (ต้องเป็น `healthy` ไม่ใช่แค่ `running`)
4. เช็คค่า env ที่ policy-api ใช้จริงตรงกับ `infra/docker/.env` หรือไม่ (โดยเฉพาะ `SPRING_DATASOURCE_URL`, user/password)
5. ถ้า log ขึ้น migration error (เช่น Flyway/Liquibase) — ดู message ว่า migration ตัวไหน fail แล้วแก้ script หรือ rollback

### Production (Lightsail/EC2)
1. SSH เข้าเครื่อง
2. `docker compose ps`
3. `docker compose logs policy-api --tail=200`
4. ตรวจ `SPRING_DATASOURCE_URL` ใน environment ของ production (อาจต่างจาก local — เช่น ชี้ไป RDS)
5. ตรวจ PostgreSQL container/RDS instance และ migration log
6. เช็ค disk space: `df -h` — ถ้าใกล้เต็ม เคลียร์ docker image เก่าก่อน (`docker system prune`) ก่อนพิจารณาขยาย disk
7. แก้ config แล้ว restart หรือ rollback ไป image version ก่อนหน้าถ้าจำเป็น

## Verification
หลังแก้แล้ว ต้องเห็นผลนี้ก่อนปิดเคส:
```bash
curl -s http://localhost:8081/actuator/health | jq
```
คาดหวัง: `{"status":"UP", "components": {"db": {"status":"UP"}, "diskSpace": {"status":"UP"}}}`

## Escalation
- โปรเจกต์นี้เป็น solo project — ถ้าแก้ตาม Steps แล้วยังไม่หายภายใน 30 นาที ให้บันทึกอาการ + log ที่เจอเพิ่มไว้ในไฟล์นี้ (เพิ่ม section "Known Issues") ก่อนลอง rollback
- ถ้า DB volume เสียหายจริง (ไม่ใช่แค่ config ผิด) ให้ restore จาก backup ล่าสุด — TODO: เขียน runbook แยกสำหรับ backup/restore เมื่อมี backup strategy จริงบน production
