CREATE TABLE genders (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    code           VARCHAR(5)    NOT NULL,
    name_th        VARCHAR(50)   NOT NULL,
    name_en        VARCHAR(50),
    order_position INT           NOT NULL DEFAULT 0,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_genders_code UNIQUE (code)
);

COMMENT ON TABLE genders IS 'ข้อมูลอ้างอิงเพศ';

INSERT INTO genders (code, name_th, name_en, order_position, created_at, created_by, updated_at, updated_by)
VALUES
    ('M', 'ชาย',    'Male',          1, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('F', 'หญิง',    'Female',        2, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('N', 'ไม่ระบุ', 'Not Specified', 3, now(), 'SYSTEM', now(), 'SYSTEM');
