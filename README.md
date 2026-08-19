# VMI — ระบบบันทึกกรมธรรม์ภาคสมัครใจ (V-Online)

โปรเจกต์ส่วนตัวเพื่อฝึกฝนและสร้าง portfolio สำหรับก้าวสู่ Senior Full Stack — จำลองระบบบันทึกกรมธรรม์ประกันภัยรถยนต์ภาคสมัครใจ

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React + TypeScript |
| Backend | Spring Boot (Modular Monolith — ดู [ADR-0001](docs/adr/0001-use-modular-monolith.md)) |
| Database | PostgreSQL |
| Object Storage | MinIO (local) → AWS S3 (production, ดู [ADR-0004](docs/adr/0004-store-attachments-in-s3.md)) |
| Auth | Keycloak |
| Local Runtime | Docker Desktop |
| Future (ยังไม่ใช้ตอนนี้) | Cloudflare, AWS Lightsail/EC2, S3 |

## โครงสร้างโปรเจกต์

```
apps/          # แอปพลิเคชันจริง (web, policy-api)
packages/      # โค้ดที่ใช้ร่วมกันระหว่าง apps (ui, api-client) — สร้างเมื่อจำเป็นจริงเท่านั้น
infra/         # docker compose, keycloak realm, nginx, minio bootstrap
docs/          # ADR, architecture, runbooks, api-specs
scripts/       # เครื่องมือช่วย เช่น generate TypeScript client จาก OpenAPI
```

## Quick Start

ต้องมี Docker Desktop รันอยู่ก่อน แล้วดูขั้นตอนเต็มที่ [docs/runbooks/local-setup.md](docs/runbooks/local-setup.md)

```bash
cp infra/docker/.env.example infra/docker/.env
make up
```

## เอกสารสำคัญ

- **Architecture Decision Records**: [docs/adr/](docs/adr/) — ทุกการตัดสินใจทางเทคนิคที่มีผลระยะยาว ต้องมี ADR พร้อม alternatives ที่พิจารณาแล้ว (ดูเทมเพลตที่ [0000-adr-template.md](docs/adr/0000-adr-template.md))
- **Runbooks**: [docs/runbooks/](docs/runbooks/) — ขั้นตอนแก้ปัญหาที่เกิดซ้ำได้ (ดูเทมเพลตที่ [0000-runbook-template.md](docs/runbooks/0000-runbook-template.md))
- **Architecture**: [docs/architecture/system-context.md](docs/architecture/system-context.md)

## Roadmap

เฟสปัจจุบันเน้น local dev ด้วย Docker Desktop + MinIO ให้ใช้งานได้จริงก่อน ส่วน Cloudflare / AWS Lightsail-EC2 / S3 เป็นเป้าหมายระยะถัดไปเมื่อ core feature (บันทึก/ค้นหา/แนบเอกสารกรมธรรม์) เสร็จสมบูรณ์
