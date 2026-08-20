-- ยกเลิก car_usage_types (V6) — ตรวจสอบกับข้อมูลจริงจาก legacy แล้วพบว่าระบบไม่มีตาราง
-- รหัสละเอียด 110/320 แยกต่างหาก ใช้แค่ car_usage_categories (จาก legacy CarUsageType) พอ
DROP TABLE car_usage_types;
