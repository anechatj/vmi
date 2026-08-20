-- seed จากข้อมูลจริง legacy master.CarPowerType (2 แถว)
INSERT INTO car_powertrain_types (code, name_th, name_en, order_position, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('ICE', 'รถสันดาป', 'Internal Combustion', 1, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('EV', 'รถไฟฟ้า', 'Electric Vehicle', 2, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
