CREATE TABLE car_license_provinces (
    id             UUID          PRIMARY KEY DEFAULT uuidv7(),
    code           VARCHAR(10)   NOT NULL,
    name_th        VARCHAR(150)  NOT NULL,
    name_en        VARCHAR(150),
    order_position INT           NOT NULL DEFAULT 0,
    active_flag    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    created_by     VARCHAR(100)  NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    updated_by     VARCHAR(100)  NOT NULL,
    version        BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_car_license_provinces_code UNIQUE (code)
);

COMMENT ON TABLE car_license_provinces IS 'ข้อมูลอ้างอิงทะเบียนจังหวัดรถ — แยกจาก provinces เพราะมี code มากกว่า 77 จังหวัด (เช่น เบตง) — ยังไม่มี seed data จริง รอรายการจากธุรกิจ';
