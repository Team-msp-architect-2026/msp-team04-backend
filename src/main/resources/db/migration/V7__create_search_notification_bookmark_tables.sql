CREATE TABLE bookmark (
                          id BIGSERIAL PRIMARY KEY,

                          user_id BIGINT NOT NULL,
                          program_id BIGINT NOT NULL,

                          created_at TIMESTAMP NOT NULL DEFAULT now(),

                          CONSTRAINT fk_bookmark_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id),

                          CONSTRAINT fk_bookmark_program
                              FOREIGN KEY (program_id)
                                  REFERENCES program(id),

                          CONSTRAINT uq_bookmark_user_program
                              UNIQUE (user_id, program_id)
);

CREATE INDEX idx_bookmark_user
    ON bookmark(user_id);

CREATE INDEX idx_bookmark_program
    ON bookmark(program_id);


CREATE TABLE search_history (
                                id BIGSERIAL PRIMARY KEY,

                                user_id BIGINT NOT NULL,
                                keyword VARCHAR(255) NOT NULL,
                                searched_at TIMESTAMP NOT NULL DEFAULT now(),

                                CONSTRAINT fk_search_history_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
);

CREATE INDEX idx_search_history_user
    ON search_history(user_id);

CREATE INDEX idx_search_history_searched_at
    ON search_history(searched_at);


CREATE TABLE ai_search_suggestion (
                                      id BIGSERIAL PRIMARY KEY,

                                      user_id BIGINT,
                                      suggestion_text VARCHAR(255) NOT NULL,
                                      is_global BOOLEAN NOT NULL DEFAULT false,

                                      created_at TIMESTAMP NOT NULL DEFAULT now(),

                                      CONSTRAINT fk_ai_search_suggestion_user
                                          FOREIGN KEY (user_id)
                                              REFERENCES users(id)
);

CREATE INDEX idx_ai_search_suggestion_user
    ON ai_search_suggestion(user_id);

CREATE INDEX idx_ai_search_suggestion_is_global
    ON ai_search_suggestion(is_global);


CREATE TABLE notification (
                              id BIGSERIAL PRIMARY KEY,

                              user_id BIGINT NOT NULL,

                              type VARCHAR(50) NOT NULL,
                              title VARCHAR(255) NOT NULL,
                              message TEXT,

                              reference_id BIGINT,
                              reference_type VARCHAR(50),

                              is_read BOOLEAN NOT NULL DEFAULT false,
                              created_at TIMESTAMP NOT NULL DEFAULT now(),

                              CONSTRAINT fk_notification_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id),

                              CONSTRAINT chk_notification_type
                                  CHECK (
                                      type IN (
                                               'AI_RECOMMENDATION',
                                               'DEADLINE_SOON',
                                               'APPLICATION_DONE',
                                               'PAYMENT_DONE',
                                               'BENEFIT_MATCHED',
                                               'COMMENT_ADDED',
                                               'RECRUITING_OPEN',
                                               'SYSTEM'
                                          )
                                      ),

                              CONSTRAINT chk_notification_reference_type
                                  CHECK (
                                      reference_type IS NULL
                                          OR reference_type IN (
                                                                'PROGRAM',
                                                                'POST',
                                                                'BENEFIT',
                                                                'APPLICATION',
                                                                'PAYMENT',
                                                                'COMMUNITY',
                                                                'NONE'
                                          )
                                      )
);

CREATE INDEX idx_notification_user
    ON notification(user_id);

CREATE INDEX idx_notification_is_read
    ON notification(is_read);

CREATE INDEX idx_notification_created_at
    ON notification(created_at);

CREATE INDEX idx_notification_reference
    ON notification(reference_type, reference_id);