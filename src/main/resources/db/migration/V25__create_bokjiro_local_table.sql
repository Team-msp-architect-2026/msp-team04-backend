-- V25__create_bokjiro_local_table.sql
CREATE TABLE IF NOT EXISTS bokjiro_local (
                                             id                  BIGSERIAL PRIMARY KEY,
                                             service_id          VARCHAR(100),
    service_name        VARCHAR(500),
    service_summary     TEXT,
    service_category    VARCHAR(200),
    service_type        VARCHAR(200),
    support_type        VARCHAR(200),
    target_group        VARCHAR(500),
    apply_method        VARCHAR(500),
    apply_url           VARCHAR(1000),
    contact             VARCHAR(200),
    department          VARCHAR(200),
    local_gov_name      VARCHAR(200),    -- 지자체명 (중앙부처와 다른 핵심 컬럼)
    local_gov_code      VARCHAR(50),
    interest_count      INTEGER,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_bokjiro_local_service_id ON bokjiro_local(service_id);
CREATE INDEX IF NOT EXISTS idx_bokjiro_local_gov_code   ON bokjiro_local(local_gov_code);