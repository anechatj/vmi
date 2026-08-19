# infra/keycloak

`vmi-realm.json` — realm config ที่ export จาก Keycloak (realm, client `vmi-web`, role `policy-officer`, client scopes/flows มาตรฐาน) ถูก import อัตโนมัติทุกครั้งที่รัน `docker compose up` ผ่าน flag `--import-realm` ใน `infra/docker/docker-compose.local.yml`

รายละเอียดการตั้งค่าทั้งหมดและวิธี login ทดสอบ ดูที่ [docs/runbooks/keycloak-setup.md](../../docs/runbooks/keycloak-setup.md)

## อัปเดต realm export เมื่อแก้ config ผ่าน Admin Console

1. เข้า Admin Console → Realm settings → Action → **Partial export**
2. เปิดทั้ง "Include groups and roles" และ "Include clients"
3. Export แล้วแทนที่ `vmi-realm.json` ในโฟลเดอร์นี้
4. commit ไฟล์ใหม่เข้า git

**หมายเหตุ**: Partial export **ไม่รวม user/password** (Keycloak ไม่ export user data ผ่านช่องทางนี้โดยตั้งใจ ด้วยเหตุผลด้าน security) — user ทดสอบต้องสร้างใหม่ทุกครั้งที่ลบ volume `docker compose down -v` (ดูขั้นตอนใน runbook ด้านบน) หรือจะเขียน seed script เพิ่มทีหลังก็ได้

**อย่า commit realm export ไฟล์ไหนที่มี credential จริงปนอยู่เข้า git** (ไฟล์นี้ปลอดภัย เพราะเป็นแค่ config โครงสร้าง ไม่มี secret ข้างใน)
