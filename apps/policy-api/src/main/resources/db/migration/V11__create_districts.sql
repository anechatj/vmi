CREATE TABLE districts (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    code           VARCHAR(4)    NOT NULL,
    name_th        VARCHAR(150)  NOT NULL,
    name_en        VARCHAR(150),
    province_id    UUID          NOT NULL REFERENCES provinces(id),
    order_position INT           NOT NULL DEFAULT 0,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_districts_code UNIQUE (code)
);

CREATE INDEX idx_districts_province_id ON districts(province_id);

COMMENT ON TABLE districts IS 'ข้อมูลอ้างอิงอำเภอ — code ใช้รหัสมาตรฐานกรมการปกครอง (DOPA) 4 หลัก — ยังไม่มี seed data จริง รอ import dataset ราชการ (ต้องมี provinces ครบก่อน)';
