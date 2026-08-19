# ADR-0004: Store attachments in S3-compatible object storage

## Status
Accepted

## Context
ระบบ VMI ต้องเก็บไฟล์แนบของกรมธรรม์ภาคสมัครใจ เช่น รูปถ่ายรถ, สำเนาบัตรประชาชน, PDF กรมธรรม์/ใบเสนอราคา

- ปริมาณและขนาดไฟล์: TODO — ประมาณจำนวนกรมธรรม์/เดือนและขนาดไฟล์เฉลี่ยเมื่อทราบ เพื่อประเมิน storage cost
- ระยะเวลาที่ต้องเก็บเอกสาร: TODO — ยืนยันข้อกำหนดการเก็บเอกสารกรมธรรม์ตามกฎหมาย/คปภ. ที่เกี่ยวข้อง (ถ้ามี) ก่อนตั้ง lifecycle rule จริง — ห้ามเดาตัวเลขเอง
- ต้องการ decouple การเก็บไฟล์จริงออกจาก application server เพื่อให้ scale และ backup ได้อิสระจากตัว app

## Decision
เก็บไฟล์แนบบน **S3-compatible object storage** โดยใช้ abstraction/interface เดียวกัน (S3 API) ทั้งสอง environment เพื่อสลับ provider ได้โดยไม่ต้องแก้โค้ดฝั่ง application layer:

- **Local/Dev**: MinIO — รันผ่าน `infra/docker/docker-compose.local.yml`, bootstrap bucket อัตโนมัติผ่าน `infra/minio/init-buckets.sh`
- **Production/SIT (อนาคต)**: AWS S3

เก็บเฉพาะ object key และ metadata (filename, content-type, size, ownerPolicyId) ใน PostgreSQL — **ไม่เก็บ binary ใน DB**

Bucket เป็น **private โดย default** ไม่มี public/anonymous access — การเข้าถึงไฟล์ทั้งหมดต้องผ่าน presigned URL ที่ `policy-api` สร้างให้ตอน request เท่านั้น

## Alternatives Considered

| ทางเลือก | ข้อดี | ข้อเสีย | เหตุผลที่ไม่เลือก |
|---|---|---|---|
| เก็บ binary ใน PostgreSQL (`bytea`) | Transaction เดียวกับข้อมูลกรมธรรม์, ไม่ต้องดูแล service แยก | DB โตเร็ว, backup/restore ช้าลงตามขนาดไฟล์, query performance แย่ลงเมื่อ table โต | ไม่เหมาะกับไฟล์รูป/PDF ที่จะโตต่อเนื่องตามจำนวนกรมธรรม์ |
| Local filesystem บน app server | ตั้งค่าง่ายสุดตอน dev | ไฟล์หายถ้า container ถูกลบโดยไม่มี volume ผูกถาวร, ไม่รองรับ deploy หลาย instance, ไม่มี built-in replication | ไม่ scale และเสี่ยงข้อมูลหายเมื่อ deploy จริง |
| S3-compatible object storage (MinIO local → S3 production) | Decouple จาก app server, scale อิสระ, มี lifecycle rule/versioning ในตัว, code path เดียวกันทั้ง local และ production ผ่าน S3 API | ต้องดูแล IAM/bucket policy เพิ่ม, มี moving part เพิ่มหนึ่งตัวตอน local dev | **เลือกทางนี้** — ข้อดีคุ้มกับ overhead โดยเฉพาะเมื่อต้อง deploy จริงบน AWS |

## Consequences

### Positive
- PostgreSQL ไม่โตจาก binary files, backup/restore DB เร็วและเบา
- Local (MinIO) และ Production (S3) ใช้ code path เดียวกันผ่าน S3 API — ลด environment drift
- แยก concern: `policy-api` ไม่ต้อง serve ไฟล์เอง, ลด load บน app server

### Negative / Trade-offs
- ต้องกำหนด IAM policy และ lifecycle rule ก่อน deploy จริง — ดูตัวอย่าง reference policy ที่ [`infra/minio/policy.json`](../../infra/minio/policy.json) (เป็นตัวอย่าง IAM policy สำหรับ production S3 เท่านั้น **ไม่ได้ apply กับ MinIO local อัตโนมัติ** — bucket local เป็น private by default อยู่แล้ว)
- เพิ่ม dependency ที่ต้องรันตอน local dev (MinIO container)
- ต้องออกแบบ object key naming convention ให้ชัดก่อนเริ่มเขียนโค้ด (ยังไม่มี — ดู Follow-ups)

### Follow-ups
- กำหนด object key naming convention (เช่น `policies/{policyId}/{documentType}/{uuid}.{ext}`) — พิจารณาแยกเป็น ADR ใหม่ หรือบันทึกไว้ใน `docs/api-specs`
- ยืนยันระยะเวลาการเก็บเอกสารตามข้อกำหนดจริงก่อนตั้ง lifecycle rule บน production S3
