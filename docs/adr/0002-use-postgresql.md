# ADR-0002: Use PostgreSQL

## Status
Proposed — ร่างเบื้องต้น ยังไม่ finalize (TODO: เติม context/decision จริงก่อนเริ่ม implement schema)

## Context
TODO: อธิบายลักษณะข้อมูลของระบบ (กรมธรรม์, ผู้เอาประกัน, เอกสารแนบ) ว่าเป็น relational data ที่ต้องการ ACID transaction ระดับไหน เช่น การออกกรมธรรม์ต้องเขียนหลายตารางพร้อมกันแบบ atomic หรือไม่

## Decision
TODO: ยืนยัน — ใช้ PostgreSQL เป็น primary datastore ของ `apps/policy-api`

## Alternatives Considered
| ทางเลือก | ข้อดี | ข้อเสีย | เหตุผลที่ไม่เลือก |
|---|---|---|---|
| MySQL / MariaDB | คุ้นเคยง่าย, community ใหญ่ | feature เชิง JSON/constraint บางอย่างสู้ Postgres ไม่ได้ | TODO |
| MongoDB (NoSQL) | schema ยืดหยุ่น | ข้อมูลกรมธรรม์เป็น relational โดยธรรมชาติ (policy ↔ ผู้เอาประกัน ↔ ความคุ้มครอง) ทำ join/transaction ยากกว่า | TODO |
| PostgreSQL | ACID เต็มรูปแบบ, รองรับ JSONB เมื่อต้องการ flexible field, ecosystem ที่ Spring Data JPA รองรับดี | ต้องดูแล instance เอง (ไม่มี managed service ตอน local) | **ทางเลือกที่เลือก (draft)** |

## Consequences

### Positive
- TODO

### Negative / Trade-offs
- TODO

### Follow-ups
- ออกแบบ schema เบื้องต้นสำหรับ policy/ผู้เอาประกัน/เอกสารแนบก่อนเริ่มเขียนโค้ด
