-- seed จากข้อมูลจริง legacy master.CarOwnerType (2 แถว)
INSERT INTO car_owner_types (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('I', 'บุคคลธรรมดา', 'Individual', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('P', 'นิติบุคคล', 'Juristict', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
