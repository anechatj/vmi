# infra/keycloak

วาง realm export (เช่น `vmi-realm.json`) ไว้ที่นี่สำหรับ local SSO

## การใช้งาน
1. Export realm จาก Keycloak admin console (Realm settings → Action → Partial export)
2. วางไฟล์ไว้ในโฟลเดอร์นี้
3. แก้ `command` ใน `infra/docker/docker-compose.local.yml` service `keycloak` เป็น `start-dev --import-realm`

**อย่า commit realm export ที่มี credential จริงเข้า git**
