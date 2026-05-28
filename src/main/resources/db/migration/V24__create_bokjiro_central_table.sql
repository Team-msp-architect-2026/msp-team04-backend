CREATE TABLE bokjiro_central (
                                 id BIGSERIAL PRIMARY KEY,
                                 external_source VARCHAR(100) NOT NULL,
                                 external_id VARCHAR(100) NOT NULL,
                                 service_name VARCHAR(500),
                                 service_summary TEXT,
                                 ministry VARCHAR(200),
                                 department VARCHAR(200),
                                 life_array VARCHAR(500),
                                 target_array VARCHAR(500),
                                 thema_array VARCHAR(500),
                                 online_apply VARCHAR(10),
                                 detail_link TEXT,
                                 support_cycle VARCHAR(100),
                                 provision_type VARCHAR(100),
                                 created_at TIMESTAMP,
                                 updated_at TIMESTAMP,
                                 CONSTRAINT uq_bokjiro_central UNIQUE (external_source, external_id)
);