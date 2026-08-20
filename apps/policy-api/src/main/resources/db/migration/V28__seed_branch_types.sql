-- seed จากข้อมูลจริง legacy master.BranchType (2 แถว)
INSERT INTO branch_types (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('HO', 'สำนักงานใหญ่', 'Head Office', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('BR', 'สาขาย่อย', 'Sub Branch', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
