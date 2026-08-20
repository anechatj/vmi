-- seed จากข้อมูลจริง legacy master.AddressType (6 แถว)
INSERT INTO address_types (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('CURRENT', 'ที่อยู่ปัจจุบัน', 'Current Address', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('REGISTERED', 'ที่อยู่ตามทะเบียนบ้าน', 'Registered Address', 3, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('DELIVERY', 'ที่อยู่จัดส่งเอกสาร', 'Delivery Address', 4, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('OFFICE', 'ที่อยู่ที่ทำงาน', 'Office Address', 5, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('OTHER', 'อื่นๆ', 'Other', 99, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('INSURED', 'ใช้ข้อมูลผู้เอาประกัน', 'Insured Address', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
