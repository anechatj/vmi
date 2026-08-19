# API Specs

โฟลเดอร์นี้เก็บ OpenAPI spec ของ `apps/policy-api` แบบ snapshot สำหรับอ้างอิงและ diff ประวัติการเปลี่ยนแปลง

- Spec จริง generate อัตโนมัติจากโค้ดโดย springdoc-openapi ที่ `GET /v3/api-docs` ขณะ `policy-api` รันอยู่
- Export snapshot: `curl http://localhost:8081/v3/api-docs -o docs/api-specs/openapi-$(date +%Y-%m-%d).json`
- `packages/api-client` ใช้ spec นี้ (หรือ live endpoint) ในการ generate TypeScript client ผ่าน `scripts/generate-api-client.sh`
