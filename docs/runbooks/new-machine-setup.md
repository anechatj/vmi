# New machine setup (เช่น ย้ายจากออฟฟิศไปบ้าน)

Runbook นี้เขียนไว้ให้ทั้งคนอ่านเองและสั่งให้ Claude อ่านแล้วทำตามได้เลย (`"อ่าน docs/runbooks/new-machine-setup.md แล้วตั้งเครื่องนี้ให้หน่อย"`)

ใช้ตอน: pull โปรเจกต์นี้มาทำต่อบนเครื่องใหม่ที่ไม่เคย setup มาก่อน (เช่น MacBook ส่วนตัว)

**หลักการสำคัญที่ต้องรู้ก่อนเริ่ม**: 1 เครื่อง = 1 SSH key และ 1 GPG key เสมอ (ห้าม copy private key ข้ามเครื่อง) — ดูเหตุผลเต็มได้จากประวัติแชทตอน setup เครื่องแรก หรือถามได้เลยถ้าลืม

---

## Checkpoint ที่ต้องหยุดรอ user (ทำเองใน browser ไม่ได้ให้ agent ทำแทน)
- เพิ่ม SSH public key ที่ GitHub → Settings → SSH and GPG keys
- เพิ่ม GPG public key ที่ GitHub → Settings → SSH and GPG keys (ถ้าทำ Phase 3.2)

ทุกจุดอื่นรันเป็นคำสั่งได้ต่อเนื่อง

---

## Phase 1: ติดตั้งพื้นฐาน

| เครื่องมือ | วิธีติดตั้งบน macOS |
|---|---|
| Docker Desktop | ดาวน์โหลดจาก docker.com ตามชิป (Apple Silicon / Intel) |
| Node.js LTS (v22+) | `brew install node@22` |
| JDK 21 | `brew install openjdk@21` |
| Git | มากับ macOS อยู่แล้ว |
| GnuPG (ถ้าจะทำ commit signing) | `brew install gnupg` |

เช็คว่าติดครบ:
```bash
docker --version
node --version
java -version
git --version
```

`make` มีอยู่แล้วใน macOS โดย default (ต่างจาก Windows) — ใช้ `make up`/`make down` ได้ตรงๆ ไม่ต้องเลี่ยงไป `docker compose` ตรงๆ เหมือนตอนอยู่ Windows

## Phase 2: SSH key สำหรับเครื่องนี้

```bash
ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519_vmi -C "vmi-github-macbook" -N ""
cat ~/.ssh/id_ed25519_vmi.pub
```

**หยุดตรงนี้** — เอา public key ที่ print ออกมาไปเพิ่มที่ **GitHub → Settings → SSH and GPG keys → New SSH key**:
- Title: `anecha-personal-macbook`
- Key type: Authentication Key

ตั้งค่า `~/.ssh/config` (สร้างไฟล์ถ้ายังไม่มี):
```
Host github.com
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_ed25519_vmi
    IdentitiesOnly yes
```

ทดสอบ:
```bash
ssh -T git@github.com -o StrictHostKeyChecking=accept-new
```
ต้องเห็น `Hi anechatj! You've successfully authenticated...` (exit code 1 เป็นเรื่องปกติ)

## Phase 3: Clone + git identity

```bash
git clone git@github.com:anechatj/vmi.git
cd vmi
git config user.name "anecha"
git config user.email "anecha.tj@gmail.com"
```

### 3.2 GPG signing (optional — ข้ามได้ถ้าไม่สนป้าย Verified)

```bash
gpg --batch --pinentry-mode loopback --passphrase "" \
  --quick-generate-key "anecha <anecha.tj@gmail.com>" ed25519 sign 1y
gpg --list-secret-keys --keyid-format=long anecha.tj@gmail.com
```
เอา key id (บรรทัด `sec   ed25519/<KEY_ID>`) มาใช้ต่อ:
```bash
git config user.signingkey <KEY_ID>
git config commit.gpgsign true
gpg --armor --export <KEY_ID>
```

**หยุดตรงนี้** — เอา output (PGP PUBLIC KEY BLOCK) ไปเพิ่มที่ **GitHub → Settings → SSH and GPG keys → New GPG key** (อีเมล `anecha.tj@gmail.com` ต้อง verify ในบัญชี GitHub อยู่แล้วถึงจะขึ้น Verified)

## Phase 4: เปิด infra

```bash
cp infra/docker/.env.example infra/docker/.env
make up
```

รอจน healthy ครบ:
```bash
docker compose -f infra/docker/docker-compose.local.yml --env-file infra/docker/.env ps
```
คาดหวัง: `vmi-postgres`, `vmi-minio`, `vmi-redis` ขึ้น `healthy`, `vmi-keycloak` และ `vmi-adminer` ขึ้น `Up`, `vmi-minio-init` ขึ้น `Exited (0)`

Realm `vmi` (client `vmi-web`, role `policy-officer`/`admin`) **import อัตโนมัติ** จาก `infra/keycloak/vmi-realm.json` — ไม่ต้องตั้งเองใหม่

## Phase 5: สร้าง test user (Keycloak ไม่ export user ให้ ต้องสร้างใหม่ทุกเครื่อง)

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -d "client_id=admin-cli" -d "username=admin" -d "password=changeme_local_only" -d "grant_type=password" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

curl -s -X POST "http://localhost:8080/admin/realms/vmi/users" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"username":"officer.test","email":"officer.test@vmi.local","firstName":"Test","lastName":"Officer","enabled":true,"emailVerified":true}'

USER_ID=$(curl -s -H "Authorization: Bearer $TOKEN" "http://localhost:8080/admin/realms/vmi/users?username=officer.test" \
  | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

curl -s -X PUT "http://localhost:8080/admin/realms/vmi/users/$USER_ID/reset-password" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"type":"password","value":"Test1234!","temporary":false}'

for ROLE in policy-officer admin; do
  ROLE_JSON=$(curl -s -H "Authorization: Bearer $TOKEN" "http://localhost:8080/admin/realms/vmi/roles/$ROLE")
  curl -s -X POST "http://localhost:8080/admin/realms/vmi/users/$USER_ID/role-mappings/realm" \
    -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
    -d "[$ROLE_JSON]"
done
```

ผลลัพธ์: user `officer.test` / password `Test1234!` มีทั้ง role `policy-officer` และ `admin` (credential local dev เท่านั้น ไม่ใช่ secret จริง — รายละเอียดใน [keycloak-setup.md](keycloak-setup.md))

## Phase 6: รัน apps

```bash
# Frontend — terminal 1
npm --prefix apps/web install
npm --prefix apps/web run dev
# เปิด http://localhost:5173

# Backend — terminal 2
cd apps/policy-api
./mvnw clean verify        # ครั้งแรกช้า (โหลด dependency + pull Testcontainers image)
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

## Verify ว่า setup สำเร็จจริง

```bash
curl -s http://localhost:8081/actuator/health
```
คาดหวัง `"db":{"status":"UP"}` และ `"redis":{"status":"UP"}`

เปิด `http://localhost:8081/swagger-ui.html` → กด Authorize → วาง JWT ที่ได้จาก:
```bash
curl -s -X POST http://localhost:8080/realms/vmi/protocol/openid-connect/token \
  -d "client_id=vmi-web" -d "username=officer.test" -d "password=Test1234!" -d "grant_type=password" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4
```
→ ลอง GET `/api/v1/master/coverage-types` ด้วย parameter `{"page": 0, "size": 20}` (ห้ามใส่ `sort` เป็น array ผ่าน Swagger UI — ดูเหตุผลใน [keycloak-setup.md](keycloak-setup.md) และประวัติแชท) → ควรได้ 200

---

## อ้างอิงเพิ่มเติม (รายละเอียดลึกกว่านี้)
- [local-setup.md](local-setup.md) — คำอธิบาย infra service แต่ละตัว
- [keycloak-setup.md](keycloak-setup.md) — concept OAuth2 flow, gotcha ที่เจอตอน setup
- [troubleshoot-api-unhealthy.md](troubleshoot-api-unhealthy.md) — ถ้า `/actuator/health` ไม่ขึ้น UP
- [../../apps/policy-api/README.md](../../apps/policy-api/README.md) — gotcha เฉพาะ Spring Boot 4 (Jackson 3, failsafe plugin, ฯลฯ)
