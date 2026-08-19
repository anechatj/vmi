# Keycloak setup (local)

สรุปการตั้งค่า Keycloak สำหรับ local dev — realm, client, role, test user ที่สร้างไว้ผ่าน Admin Console และวิธี login ทดสอบ

## ภาพรวม concept

| คำศัพท์ | ความหมาย |
|---|---|
| Realm | พื้นที่แยก user/config เป็นอิสระจากกัน |
| Client | แอปพลิเคชันที่ขอให้ Keycloak login ให้ |
| Role | สิทธิ์ที่ user มี |
| User | บัญชีผู้ใช้ที่ login ได้ |

Flow ที่ใช้คือ OAuth2 **Authorization Code Flow**: ผู้ใช้กด login → redirect ไปกรอก username/password ที่หน้า Keycloak (ไม่ใช่หน้าเว็บเรา) → ได้ authorization code กลับมา → แลกเป็น access token (JWT) → เรียก API พร้อม token → API ตรวจสอบ signature ด้วย public key ของ Keycloak โดยไม่ต้องถาม Keycloak ซ้ำทุก request

## Config ที่ตั้งไว้

### Realm
- **Name**: `vmi`
- Config เต็มอยู่ที่ [`infra/keycloak/vmi-realm.json`](../../infra/keycloak/vmi-realm.json) — import อัตโนมัติทุกครั้งที่ `docker compose up` (ผ่าน `--import-realm`)

### Client
| Field | Value |
|---|---|
| Client ID | `vmi-web` |
| Name | VMI Web Frontend |
| Client type | OpenID Connect |
| Client authentication | Off (**public client** — เหมาะกับ React SPA ที่ hide secret ไม่ได้) |
| Standard flow | เปิด (Authorization Code Flow) |
| Valid redirect URIs | `http://localhost:5173/*` (Vite dev server default port) |
| Web origins | `http://localhost:5173` |

> เมื่อ `apps/web` ตั้ง dev server port อื่นที่ไม่ใช่ 5173 ต้องกลับมาแก้ค่านี้ใน Admin Console (Clients → vmi-web → Login settings) แล้ว export realm ใหม่ทับไฟล์เดิม

### Roles
| Name | ใช้ทำอะไร |
|---|---|
| `policy-officer` | อ่านข้อมูล master/policy ทั่วไป — endpoint GET ทุกตัวเปิดให้ authenticated user (ทุก role) อยู่แล้ว |
| `admin` | เขียน/แก้/ลบข้อมูล master data — ผูกกับ `@PreAuthorize("hasRole('admin')")` ใน `CoverageTypeController` เป็นตัวแรก |

### Test user
| Field | Value |
|---|---|
| Username | `officer.test` |
| Password | `Test1234!` |
| Email | officer.test@vmi.local |
| Roles | `policy-officer`, `admin` (มีทั้งคู่ เพื่อทดสอบทั้ง read และ write flow จาก user เดียว) |
| Temporary password | Off (login ซ้ำได้โดยไม่ถูกบังคับเปลี่ยนรหัส) |

⚠️ **Credential นี้เป็น local dev เท่านั้น** ไม่ใช่ secret จริง — ไม่มีผลกระทบถ้าหลุด เพราะ Keycloak รันอยู่ใน Docker บนเครื่อง local ไม่เปิดสู่ network ภายนอก และ partial export **ไม่รวม user นี้ไว้** (ต้องสร้างใหม่เองถ้า `docker compose down -v`)

## ทดสอบ login flow ด้วยมือ (ไม่ต้องรอ apps/web)

เปิด URL นี้ในเบราว์เซอร์:
```
http://localhost:8080/realms/vmi/protocol/openid-connect/auth?client_id=vmi-web&redirect_uri=http://localhost:5173/&response_type=code&scope=openid
```
Login ด้วย `officer.test` / `Test1234!` แล้วสังเกต URL ที่ browser พยายาม redirect ไป (จะ error เพราะยังไม่มี dev server ที่ port 5173 — ไม่เป็นไร) จะเห็น query parameter `code=...` ติดมาด้วย ยืนยันว่า Keycloak ออก authorization code ให้จริง

## จุดที่พลาดง่าย (เจอมาแล้วตอน setup)

- **Assign role ให้ user แล้ว "No search results"** — ต้องเปลี่ยน dropdown จาก "Filter by clients" เป็น **"Filter by realm roles"** ก่อนค้นหา ไม่งั้นจะเห็นแต่ role ของ client อื่น (account, broker, realm-management)
- **Login ครั้งแรกโดน redirect ไปหน้า "Update Account Information"** — เพราะ field email เป็น required by default ของ realm ใหม่ กรอกแล้ว submit ผ่านได้ปกติ
- **`vmi_user` (PostgreSQL) ≠ `officer.test` (Keycloak)** — คนละระบบกันคนละหน้าที่ อย่าสับสน (`vmi_user` คือ DB credential ที่ `apps/policy-api` ใช้เชื่อม Postgres เท่านั้น ไม่เกี่ยวกับ login หน้าเว็บ)

## อัปเดต config ในอนาคต

ทุกครั้งที่แก้ realm/client/role ผ่าน Admin Console ให้ export ใหม่ทับ `infra/keycloak/vmi-realm.json` แล้ว commit — ดูขั้นตอนที่ [infra/keycloak/README.md](../../infra/keycloak/README.md)
