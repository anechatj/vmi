-- seed จากข้อมูลจริง legacy master.LicenseType (5 แถว)
INSERT INTO car_license_types (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('REGIST', 'รถปกติ', 'Regist Plate', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('TEMP', 'รถป้ายแดง', 'Temporary Plate', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('UNREGIST', 'รถไม่มีป้ายทะเบียน', 'Unregist Plate', 3, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('GOVT', 'รถราชการ', 'Government Plate', 4, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('FOREIGN', 'รถต่างประเทศ', 'Foreign Plate', 5, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
