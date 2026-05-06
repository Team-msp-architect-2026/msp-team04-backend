CREATE TABLE recommendation_preference (
                                           id BIGSERIAL PRIMARY KEY,
                                           user_id BIGINT NOT NULL,
                                           child_id BIGINT NOT NULL,

                                           region VARCHAR(100),
                                           monthly_budget VARCHAR(50),
                                           transport_type VARCHAR(50),
                                           move_time VARCHAR(50),
                                           online_preference VARCHAR(50),
                                           class_type VARCHAR(50),

                                           created_at TIMESTAMP NOT NULL DEFAULT now(),

                                           CONSTRAINT fk_recommendation_preference_user
                                               FOREIGN KEY (user_id)
                                                   REFERENCES users(id),

                                           CONSTRAINT fk_recommendation_preference_child
                                               FOREIGN KEY (child_id)
                                                   REFERENCES child_profile(id),

                                           CONSTRAINT chk_recommendation_monthly_budget
                                               CHECK (
                                                   monthly_budget IS NULL
                                                       OR monthly_budget IN ('FREE', '0-10', '10-20', '20+', 'ANY')
                                                   ),

                                           CONSTRAINT chk_recommendation_transport_type
                                               CHECK (
                                                   transport_type IS NULL
                                                       OR transport_type IN ('WALK', 'CAR')
                                                   ),

                                           CONSTRAINT chk_recommendation_move_time
                                               CHECK (
                                                   move_time IS NULL
                                                       OR move_time IN ('10MIN', '10-20MIN', '20MIN+', 'ANY')
                                                   ),

                                           CONSTRAINT chk_recommendation_online_preference
                                               CHECK (
                                                   online_preference IS NULL
                                                       OR online_preference IN ('ONLINE_OK', 'OFFLINE_ONLY', 'ANY')
                                                   ),

                                           CONSTRAINT chk_recommendation_class_type
                                               CHECK (
                                                   class_type IS NULL
                                                       OR class_type IN ('SMALL', 'MEDIUM', 'ONE_ON_ONE', 'ONLINE', 'VISIT')
                                                   )
);

CREATE INDEX idx_recommendation_preference_user_child_created
    ON recommendation_preference(user_id, child_id, created_at);


CREATE TABLE ai_recommendation (
                                   id BIGSERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL,
                                   child_id BIGINT NOT NULL,
                                   preference_id BIGINT NOT NULL,
                                   program_id BIGINT NOT NULL,

                                   total_score NUMERIC(5, 2) NOT NULL,
                                   score_distance NUMERIC(5, 2),
                                   score_budget NUMERIC(5, 2),
                                   score_age NUMERIC(5, 2),
                                   score_keyword NUMERIC(5, 2),
                                   score_class_type NUMERIC(5, 2),
                                   score_recruiting NUMERIC(5, 2),
                                   score_review NUMERIC(5, 2),

                                   rank_no INT,
                                   recommend_reason TEXT,
                                   is_top3 BOOLEAN NOT NULL DEFAULT false,

                                   created_at TIMESTAMP NOT NULL DEFAULT now(),

                                   CONSTRAINT fk_ai_recommendation_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES users(id),

                                   CONSTRAINT fk_ai_recommendation_child
                                       FOREIGN KEY (child_id)
                                           REFERENCES child_profile(id),

                                   CONSTRAINT fk_ai_recommendation_preference
                                       FOREIGN KEY (preference_id)
                                           REFERENCES recommendation_preference(id),

                                   CONSTRAINT fk_ai_recommendation_program
                                       FOREIGN KEY (program_id)
                                           REFERENCES program(id),

                                   CONSTRAINT uq_ai_recommendation_preference_program
                                       UNIQUE (preference_id, program_id),

                                   CONSTRAINT uq_ai_recommendation_preference_rank
                                       UNIQUE (preference_id, rank_no),

                                   CONSTRAINT chk_ai_recommendation_total_score_range
                                       CHECK (total_score >= 0 AND total_score <= 100)
);

CREATE INDEX idx_ai_recommendation_user_child_preference
    ON ai_recommendation(user_id, child_id, preference_id);

CREATE INDEX idx_ai_recommendation_preference_rank
    ON ai_recommendation(preference_id, rank_no);

CREATE INDEX idx_ai_recommendation_program
    ON ai_recommendation(program_id);