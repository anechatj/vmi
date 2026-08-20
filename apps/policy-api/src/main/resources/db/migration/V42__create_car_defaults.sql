-- ค่า default สเปครถตามรหัสการใช้รถละเอียด (110/320 ฯลฯ) — ไม่มี master table สำหรับรหัสนี้เอง
-- (ตรวจสอบแล้วตอนดูไฟล์ masters.sql — ระบบจริงไม่มีตาราง lookup สำหรับรหัสนี้) จึงเก็บเป็น VARCHAR ธรรมดา ไม่ FK
CREATE TABLE car_defaults (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    car_usage_code VARCHAR(10)   NOT NULL,
    seat           INT,
    car_size       INT,
    weight         INT,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT uq_car_defaults_usage_code UNIQUE (car_usage_code)
);

COMMENT ON TABLE car_defaults IS 'ค่า default สเปครถ (ที่นั่ง/ขนาด/น้ำหนัก) สำหรับ autofill ตามรหัสการใช้รถ';

-- seed จากข้อมูลจริง legacy master.CarDefault (2 แถว)
INSERT INTO car_defaults (car_usage_code, seat, car_size, weight, active_flag, created_at, created_by, updated_at, updated_by)
VALUES
    ('110', 7, 2000, 0, TRUE, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('320', 3, 0, 3, TRUE, now(), 'SYSTEM', now(), 'SYSTEM');
