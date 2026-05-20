CREATE TABLE gov_benefits (
                              id BIGSERIAL PRIMARY KEY,

                              external_source VARCHAR(50) NOT NULL,
                              external_id VARCHAR(100) NOT NULL,

                              service_name VARCHAR(500),
                              summary TEXT,
                              support_content TEXT,
                              target_audience TEXT,
                              organization VARCHAR(300),
                              apply_method TEXT,
                              apply_url TEXT,
                              service_category VARCHAR(200),

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT uk_gov_benefits_external UNIQUE (external_source, external_id)
);