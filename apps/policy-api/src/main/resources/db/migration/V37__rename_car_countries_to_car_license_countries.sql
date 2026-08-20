-- เปลี่ยนชื่อและความหมาย: car_countries (ตั้งใจไว้เป็น "ประเทศผู้ผลิตรถ") ไม่มีข้อมูลจริงรองรับ
-- แต่พบว่า legacy มี master.LicenseCountry ("ประเทศของทะเบียนรถ") อยู่จริง — ใช้ table เดิม เปลี่ยนชื่อ/เติม column ให้ตรงความหมายใหม่
ALTER TABLE car_countries RENAME TO car_license_countries;
ALTER TABLE car_license_countries RENAME CONSTRAINT uq_car_countries_code TO uq_car_license_countries_code;
ALTER TABLE car_license_countries ADD COLUMN country_filter VARCHAR(10);

COMMENT ON TABLE car_license_countries IS 'ข้อมูลอ้างอิงประเทศของทะเบียนรถ (รถจดทะเบียนต่างประเทศ) — country_filter ใช้จับคู่กับ license_type_config.country_filter (ALL/THA/NON_THA)';

-- seed จากข้อมูลจริง legacy master.LicenseCountry (20 แถว)
INSERT INTO car_license_countries (code, name_th, name_en, order_position, active_flag, country_filter, created_at, created_by, updated_at, updated_by)
VALUES
    ('THA', 'ไทย', 'Thailand', 1, TRUE, 'THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('MMR', 'เมียนมาร์', 'Myanmar', 2, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('LAO', 'ลาว', 'Laos', 3, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('KHM', 'กัมพูชา', 'Cambodia', 4, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('VNM', 'เวียดนาม', 'Vietnam', 5, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('MYS', 'มาเลเซีย', 'Malaysia', 6, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('SGP', 'สิงคโปร์', 'Singapore', 7, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('IDN', 'อินโดนีเซีย', 'Indonesia', 8, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('PHL', 'ฟิลิปปินส์', 'Philippines', 9, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('BRN', 'บรูไน', 'Brunei', 10, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('CHN', 'จีน', 'China', 11, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('JPN', 'ญี่ปุ่น', 'Japan', 12, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('KOR', 'เกาหลีใต้', 'South Korea', 13, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('USA', 'สหรัฐอเมริกา', 'USA', 14, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('GBR', 'สหราชอาณาจักร', 'UK', 15, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('AUS', 'ออสเตรเลีย', 'Australia', 16, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('IND', 'อินเดีย', 'India', 17, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('DEU', 'เยอรมนี', 'Germany', 18, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('FRA', 'ฝรั่งเศส', 'France', 19, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM'),
    ('OTH', 'อื่นๆ', 'Other', 99, TRUE, 'NON_THA', now(), 'SYSTEM', now(), 'SYSTEM');
