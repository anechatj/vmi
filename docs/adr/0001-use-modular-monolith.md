# ADR-0001: Use Modular Monolith

## Status
Proposed — ร่างเบื้องต้น ยังไม่ finalize (TODO: เติม context/decision จริงก่อนเริ่ม implement `apps/policy-api`)

## Context
TODO: อธิบายเหตุผลที่พิจารณาโครงสร้างนี้ เช่น เป็นโปรเจกต์คนเดียว, ต้องการ deploy ง่ายบน Lightsail ก่อนพิจารณาแตก microservice, domain (กรมธรรม์/เอกสาร/การชำระเงิน) ยังไม่นิ่งพอจะแบ่งเป็น service แยก

## Decision
TODO: ยืนยัน — สร้าง `apps/policy-api` เป็น Spring Boot application เดียว แต่แบ่ง package ภายในตาม domain (เช่น `policy`, `payment`, `document`) โดยมีขอบเขตชัดเจนระหว่าง module เพื่อให้แตกเป็น service แยกได้ในอนาคตถ้าจำเป็น

## Alternatives Considered
| ทางเลือก | ข้อดี | ข้อเสีย | เหตุผลที่ไม่เลือก |
|---|---|---|---|
| Microservices ตั้งแต่แรก | scale และ deploy แยกส่วนได้อิสระ | operational overhead สูงเกินสำหรับทีม/โปรเจกต์ขนาดนี้ (CI/CD, service discovery, network latency) | TODO: ยืนยันเหตุผล |
| Modular Monolith | deploy ง่าย (1 artifact), แตกเป็น service ทีหลังได้ถ้า module boundary ชัด | ต้องคุม module boundary เองด้วยวินัย ไม่มี compiler บังคับข้าม package โดย default | **ทางเลือกที่เลือก (draft)** |

## Consequences

### Positive
- TODO

### Negative / Trade-offs
- TODO

### Follow-ups
- กำหนด package structure จริงตอนเริ่มเขียน `apps/policy-api`
- พิจารณาใช้เครื่องมือ (เช่น ArchUnit) บังคับ module boundary เมื่อโค้ดเริ่มโต
