# packages/api-client

TypeScript client ที่ generate อัตโนมัติจาก OpenAPI spec ของ `apps/policy-api`

## Generate
```bash
make generate-client
# หรือ
bash scripts/generate-api-client.sh
```
ต้องมี `apps/policy-api` รันอยู่ก่อน (default: `http://localhost:8081/v3/api-docs`)

## หมายเหตุ
- โฟลเดอร์ `generated/` **ไม่ commit เข้า git** (ดู `.gitignore`) — generate ใหม่ทุกครั้งหลัง pull หรือหลัง API เปลี่ยน
- `apps/web` import client จากที่นี่แทนการเขียน fetch call เอง เพื่อให้ type ตรงกับ backend เสมอ
