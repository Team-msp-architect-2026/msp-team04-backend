CREATE TABLE IF NOT EXISTS welfare_unified (
                                               id             BIGSERIAL PRIMARY KEY,
                                               source         VARCHAR(50) NOT NULL,
    original_id    VARCHAR(100),
    service_id     VARCHAR(100),
    title          TEXT,
    description    TEXT,
    target_group   TEXT,
    support_type   TEXT,
    apply_method   TEXT,
    apply_url      TEXT,
    department     TEXT,
    local_gov_name TEXT,
    is_local       BOOLEAN DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_welfare_unified_source   ON welfare_unified(source);
CREATE INDEX IF NOT EXISTS idx_welfare_unified_is_local ON welfare_unified(is_local);