CREATE TABLE sub_districts (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    code           VARCHAR(6)    NOT NULL,
    name_th        VARCHAR(150)  NOT NULL,
    name_en        VARCHAR(150),
    district_id    UUID          NOT NULL REFERENCES districts(id),
    province_id    UUID          NOT NULL REFERENCES provinces(id),
    order_position INT           NOT NULL DEFAULT 0,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_sub_districts_code UNIQUE (code)
);

CREATE INDEX idx_sub_districts_district_id ON sub_districts(district_id);
CREATE INDEX idx_sub_districts_province_id ON sub_districts(province_id);

COMMENT ON TABLE sub_districts IS 'ข้อมูลอ้างอิงตำบล — code ใช้รหัสมาตรฐานกรมการปกครอง (DOPA) 6 หลัก — province_id เก็บซ้ำจาก district เพื่อ query ข้ามชั้นได้โดยตรง — ยังไม่มี seed data จริง รอ import dataset ราชการ';
