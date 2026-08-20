-- seed จากข้อมูลจริง legacy master.CarUsageType (4 แถว) — ตรงกับ car_usage_categories ของเรา (กลุ่มกว้าง)
INSERT INTO car_usage_categories (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('I', 'ส่วนบุคคล', 'Private Vehicle', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('R', 'รับจ้าง/ให้เช่า', 'Commercial Vehicle', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('P', 'สาธารณะ', 'Public Service Vehicle', 3, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('A', 'รับจ้าง/ให้เช่า/สาธารณะ', 'Commercial/Public Service Vehicle', 4, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
