# เปลี่ยน local dev credentials (ให้จำง่าย)

Runbook นี้เขียนไว้ให้ทั้งคนอ่านเองและสั่งให้ Claude อ่านแล้วทำตามได้เลย (`"อ่าน docs/runbooks/change-local-credentials.md แล้วเปลี่ยน credential ให้เหมือนเครื่องหลักให้หน่อย"`)

ใช้ตอน: ทำตามนี้บนเครื่องอื่น (เช่น notebook ที่ออฟฟิศ) เพื่อให้ credential ตรงกับเครื่องหลัก หรือจะเปลี่ยนใหม่ทั้งหมดก็ทำตามขั้นตอนเดียวกันนี้ได้

**ค่าปัจจุบันหลังเปลี่ยน (เครื่องหลัก):**

| ระบบ | Username | Password |
|---|---|---|
| เว็บแอป VMI (`localhost:5173`) | `officer.test` (เดิม) หรือ `admin` (ใหม่) | `Test1234!` |
| Keycloak Admin Console (`localhost:8080`) | `admin` | `Test1234!` |
| PostgreSQL | `admin` | `Test1234!` |

⚠️ **คำเตือน**: หลังเปลี่ยนแล้ว PostgreSQL role กับ Keycloak user ใน realm `vmi` ชื่อ **`admin` เหมือนกันทั้งคู่** แต่เป็นคนละระบบกันคนละหน้าที่โดยสิ้นเชิง — `admin` (PostgreSQL) คือ DB credential ที่ `apps/policy-api` ใช้เชื่อม Postgres เท่านั้น ส่วน `admin` (Keycloak realm `vmi`) คือ login หน้าเว็บ อย่าสับสน (เดิมคนละชื่อ `vmi_user` vs `officer.test` เลยไม่งง — ตอนนี้ต้องระวังเป็นพิเศษ)

ทุกขั้นตอนรันเป็นคำสั่งได้ต่อเนื่อง ไม่มี checkpoint ที่ต้องรอ browser

---

## Phase 1: เปลี่ยนรหัส Keycloak Admin Console (realm `master`)

```bash
MASTER_TOKEN=$(curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -d "client_id=admin-cli" -d "username=admin" -d "password=changeme_local_only" -d "grant_type=password" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

MASTER_ADMIN_ID=$(curl -s -H "Authorization: Bearer $MASTER_TOKEN" "http://localhost:8080/admin/realms/master/users?username=admin&exact=true" \
  | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

curl -s -X PUT "http://localhost:8080/admin/realms/master/users/$MASTER_ADMIN_ID/reset-password" \
  -H "Authorization: Bearer $MASTER_TOKEN" -H "Content-Type: application/json" \
  -d '{"type":"password","value":"Test1234!","temporary":false}'
```

ทดสอบ:
```bash
curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -d "client_id=admin-cli" -d "username=admin" -d "password=Test1234!" -d "grant_type=password" \
  -o /dev/null -w "HTTP %{http_code}\n"
```
คาดหวัง `HTTP 200`

## Phase 2: สร้าง user `admin` ใหม่ใน realm `vmi` (login หน้าเว็บ)

user เดิม `officer.test` / `Test1234!` ยังใช้ได้ปกติ — นี่คือการ**เพิ่ม**อีก 1 คน ไม่ใช่แทนที่

```bash
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -d "client_id=admin-cli" -d "username=admin" -d "password=Test1234!" -d "grant_type=password" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

curl -s -X POST "http://localhost:8080/admin/realms/vmi/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@vmi.local","firstName":"Admin","lastName":"User","enabled":true,"emailVerified":true}'

NEW_USER_ID=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" "http://localhost:8080/admin/realms/vmi/users?username=admin&exact=true" \
  | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

curl -s -X PUT "http://localhost:8080/admin/realms/vmi/users/$NEW_USER_ID/reset-password" \
  -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json" \
  -d '{"type":"password","value":"Test1234!","temporary":false}'

ROLE_JSON=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" "http://localhost:8080/admin/realms/vmi/roles/admin")
curl -s -X POST "http://localhost:8080/admin/realms/vmi/users/$NEW_USER_ID/role-mappings/realm" \
  -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json" \
  -d "[$ROLE_JSON]"
```

ทดสอบ:
```bash
curl -s -X POST http://localhost:8080/realms/vmi/protocol/openid-connect/token \
  -d "client_id=vmi-web" -d "username=admin" -d "password=Test1234!" -d "grant_type=password" \
  -o /dev/null -w "HTTP %{http_code}\n"
```
คาดหวัง `HTTP 200`

## Phase 3: เปลี่ยน PostgreSQL role `vmi_user` → `admin`

Postgres ไม่ยอมให้ role rename ตัวเอง ต้องสร้าง superuser ชั่วคราวมาทำแทนแล้วลบทิ้ง (ใส่ port ให้ตรงกับ `POSTGRES_PORT` ใน `.env` ของเครื่องนั้นๆ — เครื่องหลักใช้ `5433`):

```bash
DB_PORT=5433   # แก้ตาม POSTGRES_PORT ใน infra/docker/.env ของเครื่องนี้

PGPASSWORD=changeme_local_only psql -h localhost -p $DB_PORT -U vmi_user -d vmi \
  -c "CREATE ROLE temp_admin WITH LOGIN SUPERUSER PASSWORD 'temp_pw_local_only';"

PGPASSWORD=temp_pw_local_only psql -h localhost -p $DB_PORT -U temp_admin -d vmi \
  -c "ALTER USER vmi_user RENAME TO admin;"

PGPASSWORD=temp_pw_local_only psql -h localhost -p $DB_PORT -U temp_admin -d vmi \
  -c "ALTER USER admin WITH PASSWORD 'Test1234!';"

PGPASSWORD='Test1234!' psql -h localhost -p $DB_PORT -U admin -d vmi \
  -c "DROP ROLE temp_admin;"
```

ทดสอบ:
```bash
PGPASSWORD='Test1234!' psql -h localhost -p $DB_PORT -U admin -d vmi -c "select current_user;"
```
คาดหวังเห็น `admin`

## Phase 4: sync ไฟล์ config

ถ้า pull โค้ดล่าสุดมาแล้ว ไฟล์เหล่านี้ตั้งค่า default เป็น `admin`/`Test1234!` ให้อยู่แล้ว — เช็คว่าตรงจริง:
- [`infra/docker/.env.example`](../../infra/docker/.env.example) — `POSTGRES_USER=admin`, `POSTGRES_PASSWORD=Test1234!`, `KEYCLOAK_ADMIN_PASSWORD=Test1234!`
- [`apps/policy-api/src/main/resources/application-local.yaml`](../../apps/policy-api/src/main/resources/application-local.yaml) — `DB_USERNAME:admin`, `DB_PASSWORD:Test1234!`

ถ้าเครื่องนี้มี `infra/docker/.env` ของตัวเองอยู่แล้ว (ไม่ commit เข้า git) ต้องแก้มือด้วย:
```bash
sed -i '' 's/^POSTGRES_USER=.*/POSTGRES_USER=admin/' infra/docker/.env
sed -i '' 's/^POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=Test1234!/' infra/docker/.env
sed -i '' 's/^KEYCLOAK_ADMIN_PASSWORD=.*/KEYCLOAK_ADMIN_PASSWORD=Test1234!/' infra/docker/.env
```

## Phase 5: restart backend

Devtools ไม่เฝ้าดู `src/main/resources` โดยตรง ต้อง restart เอง ไม่ใช่รอ auto-reload:
```bash
# กด Ctrl+C ที่ terminal ที่รัน spring-boot:run อยู่ แล้วรันใหม่
cd apps/policy-api
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

## Verification ว่าสำเร็จจริง

```bash
curl -s http://localhost:8081/actuator/health
```
คาดหวัง `"db":{"status":"UP"}`

---

## ที่ต้องแก้เองใน GUI (ทำแทนไม่ได้)

- **pgAdmin 4** — ถ้าเคย register server ไว้แล้ว ต้องแก้ username/password ใน server properties เอง (Connection tab) เป็น `admin` / `Test1234!`
- **Adminer** — ไม่ต้องแก้อะไร กรอก username/password ใหม่ตอน login รอบถัดไปได้เลย ไม่มีการจำค่าเดิมไว้

## อ้างอิงเพิ่มเติม
- [new-machine-setup.md](new-machine-setup.md) — setup เครื่องใหม่ทั้งหมดตั้งแต่ต้น (Phase 5 สร้าง `officer.test`)
- [keycloak-setup.md](keycloak-setup.md) — concept OAuth2 flow, ความแตกต่างระหว่าง DB credential กับ Keycloak login
