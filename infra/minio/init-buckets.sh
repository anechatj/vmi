#!/bin/sh
set -e

# สร้าง bucket สำหรับเก็บไฟล์แนบกรมธรรม์ (private by default)
# การเข้าถึงไฟล์ทำผ่าน presigned URL จาก policy-api เท่านั้น — ไม่มีการ apply public/anonymous policy ที่นี่
# อ้างอิงการตัดสินใจใน docs/adr/0004-store-attachments-in-s3.md
# ดู policy.json ในโฟลเดอร์นี้สำหรับตัวอย่าง IAM policy ตอน deploy จริงบน AWS S3 (ไม่เกี่ยวกับ script นี้)

mc alias set local http://minio:9000 "${MINIO_ROOT_USER}" "${MINIO_ROOT_PASSWORD}"

if mc ls "local/${MINIO_BUCKET_NAME}" >/dev/null 2>&1; then
  echo "Bucket ${MINIO_BUCKET_NAME} already exists, skipping create."
else
  echo "Creating private bucket ${MINIO_BUCKET_NAME}..."
  mc mb "local/${MINIO_BUCKET_NAME}"
fi

echo "MinIO bucket setup complete (private, access via presigned URL only)."
