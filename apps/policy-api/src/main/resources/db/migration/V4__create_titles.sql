CREATE TABLE titles (
    id                      UUID          PRIMARY KEY DEFAULT uuidv7(),
    code                    VARCHAR(10)   NOT NULL,
    name_th                 VARCHAR(50)   NOT NULL,
    name_en                 VARCHAR(50),
    short_th                VARCHAR(20),
    identification_type_id  UUID          NOT NULL REFERENCES identification_types(id),
    gender_id               UUID          NOT NULL REFERENCES genders(id),
    order_position          INT           NOT NULL DEFAULT 0,
    active_flag             BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ   NOT NULL,
    created_by              VARCHAR(100)  NOT NULL,
    updated_at              TIMESTAMPTZ   NOT NULL,
    updated_by              VARCHAR(100)  NOT NULL,
    version                 BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_titles_code UNIQUE (code)
);

CREATE INDEX idx_titles_identification_type_id ON titles(identification_type_id);
CREATE INDEX idx_titles_gender_id ON titles(gender_id);

COMMENT ON TABLE titles IS 'ข้อมูลอ้างอิงคำนำหน้าชื่อ เช่น นาย, นาง, นางสาว, บริษัท';

INSERT INTO titles (code, name_th, name_en, short_th, identification_type_id, gender_id, order_position, created_at, created_by, updated_at, updated_by)
VALUES
    ('76', 'นาง',    'Mrs.',    'นาง',  (SELECT id FROM identification_types WHERE code = 'INDIVIDUAL'), (SELECT id FROM genders WHERE code = 'F'), 1, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('77', 'นางสาว', 'Miss',    'น.ส.', (SELECT id FROM identification_types WHERE code = 'INDIVIDUAL'), (SELECT id FROM genders WHERE code = 'F'), 2, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('78', 'นาย',    'Mr.',     'นาย',  (SELECT id FROM identification_types WHERE code = 'INDIVIDUAL'), (SELECT id FROM genders WHERE code = 'M'), 3, now(), 'SYSTEM', now(), 'SYSTEM'),
    ('87', 'บริษัท', 'Company', 'บ.',   (SELECT id FROM identification_types WHERE code = 'CORPORATE'), (SELECT id FROM genders WHERE code = 'N'), 4, now(), 'SYSTEM', now(), 'SYSTEM');
