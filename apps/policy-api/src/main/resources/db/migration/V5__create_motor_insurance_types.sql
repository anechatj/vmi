CREATE TABLE motor_insurance_types (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    code           VARCHAR(10)   NOT NULL,
    name_th        VARCHAR(50)   NOT NULL,
    name_en        VARCHAR(50),
    order_position INT           NOT NULL DEFAULT 0,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_motor_insurance_types_code UNIQUE (code)
);

COMMENT ON TABLE motor_insurance_types IS 'ข้อมูลอ้างอิงประเภทประกันภัยรถยนต์ เช่น ชั้น 1, ชั้น 2+, ชั้น 3+, ชั้น 3';

-- ข้อมูลจริงจากระบบเดิม (legacy master.MotorInsuranceType) — code คือค่าที่ Package.i_pol_type
-- อ้างอิงจริงในระบบเก่า จึงใช้ต่อเพื่อให้ map ข้อมูลเก่า-ใหม่ตรงกัน
INSERT INTO motor_insurance_types (code, name_th, name_en, order_position, created_at, created_by, updated_at, updated_by)
VALUES
    ('1',  'ชั้น 1',  'Type 1',  1, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('2',  'ชั้น 2',  'Type 2',  2, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('2+', 'ชั้น 2+', 'Type 2+', 3, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('3',  'ชั้น 3',  'Type 3',  4, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('3+', 'ชั้น 3+', 'Type 3+', 5, now(), 'SYSTEM', now(), 'SYSTEM');
