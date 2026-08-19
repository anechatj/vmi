CREATE TABLE coverage_type (
    id          UUID PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    sort_order  INT          NOT NULL DEFAULT 0,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_coverage_type_code UNIQUE (code)
);

COMMENT ON TABLE coverage_type IS 'ข้อมูลอ้างอิงประเภทความคุ้มครอง เช่น ชั้น 1, ชั้น 2+, ชั้น 3+, ชั้น 3';
