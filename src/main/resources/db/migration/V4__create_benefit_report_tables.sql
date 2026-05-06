CREATE TABLE benefit_master (
                                id BIGSERIAL PRIMARY KEY,

                                benefit_name VARCHAR(255) NOT NULL,
                                benefit_type VARCHAR(50),
                                support_amount INT,
                                support_description VARCHAR(500),
                                apply_link VARCHAR(500),

                                min_age INT,
                                max_age INT,
                                condition_description TEXT,
                                region VARCHAR(100),
                                is_active BOOLEAN NOT NULL DEFAULT true,

                                external_source VARCHAR(100),
                                external_id VARCHAR(255),
                                last_synced_at TIMESTAMP,

                                created_at TIMESTAMP NOT NULL DEFAULT now(),
                                updated_at TIMESTAMP,

                                CONSTRAINT uq_benefit_master_external_source_id
                                    UNIQUE (external_source, external_id),

                                CONSTRAINT chk_benefit_type
                                    CHECK (
                                        benefit_type IS NULL
                                            OR benefit_type IN ('ALLOWANCE', 'VOUCHER', 'FREE_SERVICE', 'DISCOUNT')
                                        ),

                                CONSTRAINT chk_benefit_support_amount_non_negative
                                    CHECK (
                                        support_amount IS NULL
                                            OR support_amount >= 0
                                        ),

                                CONSTRAINT chk_benefit_master_age_range
                                    CHECK (
                                        min_age IS NULL
                                            OR max_age IS NULL
                                            OR min_age <= max_age
                                        ),

                                CONSTRAINT chk_benefit_master_external_pair
                                    CHECK (
                                        (external_source IS NULL AND external_id IS NULL)
                                            OR
                                        (external_source IS NOT NULL AND external_id IS NOT NULL)
                                        )
);

CREATE INDEX idx_benefit_master_type
    ON benefit_master(benefit_type);

CREATE INDEX idx_benefit_master_region
    ON benefit_master(region);

CREATE INDEX idx_benefit_master_is_active
    ON benefit_master(is_active);


CREATE TABLE benefit_match (
                               id BIGSERIAL PRIMARY KEY,

                               user_id BIGINT NOT NULL,
                               child_id BIGINT NOT NULL,
                               benefit_id BIGINT NOT NULL,

                               match_status VARCHAR(50) NOT NULL,
                               expected_monthly_saving INT,
                               matched_at TIMESTAMP NOT NULL DEFAULT now(),

                               CONSTRAINT fk_benefit_match_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id),

                               CONSTRAINT fk_benefit_match_child
                                   FOREIGN KEY (child_id)
                                       REFERENCES child_profile(id),

                               CONSTRAINT fk_benefit_match_benefit
                                   FOREIGN KEY (benefit_id)
                                       REFERENCES benefit_master(id),

                               CONSTRAINT uq_benefit_match_child_benefit
                                   UNIQUE (child_id, benefit_id),

                               CONSTRAINT chk_benefit_match_status
                                   CHECK (
                                       match_status IN ('APPLICABLE', 'NOT_ELIGIBLE')
                                       ),

                               CONSTRAINT chk_benefit_match_expected_saving_non_negative
                                   CHECK (
                                       expected_monthly_saving IS NULL
                                           OR expected_monthly_saving >= 0
                                       )
);

CREATE INDEX idx_benefit_match_user_child
    ON benefit_match(user_id, child_id);

CREATE INDEX idx_benefit_match_benefit
    ON benefit_match(benefit_id);


CREATE TABLE ai_report (
                           id BIGSERIAL PRIMARY KEY,

                           child_id BIGINT NOT NULL,

                           total_support_count INT NOT NULL DEFAULT 0,
                           total_free_program_count INT NOT NULL DEFAULT 0,
                           total_recommend_count INT NOT NULL DEFAULT 0,
                           total_monthly_saving INT NOT NULL DEFAULT 0,

                           ai_match_score NUMERIC(5, 2),
                           summary_message TEXT,

                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                           updated_at TIMESTAMP,

                           CONSTRAINT fk_ai_report_child
                               FOREIGN KEY (child_id)
                                   REFERENCES child_profile(id),

                           CONSTRAINT uq_ai_report_child
                               UNIQUE (child_id),

                           CONSTRAINT chk_ai_report_total_support_count
                               CHECK (total_support_count >= 0),

                           CONSTRAINT chk_ai_report_total_free_program_count
                               CHECK (total_free_program_count >= 0),

                           CONSTRAINT chk_ai_report_total_recommend_count
                               CHECK (total_recommend_count >= 0),

                           CONSTRAINT chk_ai_report_total_monthly_saving
                               CHECK (total_monthly_saving >= 0),

                           CONSTRAINT chk_ai_report_match_score_range
                               CHECK (
                                   ai_match_score IS NULL
                                       OR (ai_match_score >= 0 AND ai_match_score <= 100)
                                   )
);