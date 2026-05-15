CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id BIGSERIAL PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              token TEXT NOT NULL,
                                              expired_at TIMESTAMP NOT NULL,

                                              CONSTRAINT uk_refresh_tokens_user_id UNIQUE (user_id),
    CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );