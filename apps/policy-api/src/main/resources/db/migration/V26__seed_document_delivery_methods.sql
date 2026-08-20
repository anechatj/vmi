-- seed จากข้อมูลจริง legacy master.DocumentDeliveryType (2 แถว)
INSERT INTO document_delivery_methods (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('DELIVER', 'ส่ง', 'Deliver', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('COLLECT', 'เก็บ', 'Collect', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
