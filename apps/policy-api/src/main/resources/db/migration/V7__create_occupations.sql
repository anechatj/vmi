CREATE TABLE occupations (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    code           VARCHAR(20)   NOT NULL,
    name_th        VARCHAR(150)  NOT NULL,
    name_en        VARCHAR(150),
    order_position INT           NOT NULL DEFAULT 0,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_occupations_code UNIQUE (code)
);

COMMENT ON TABLE occupations IS 'ข้อมูลอ้างอิงอาชีพผู้เอาประกัน — ยังไม่มี seed data จริง รอรายการอาชีพจากธุรกิจ';
