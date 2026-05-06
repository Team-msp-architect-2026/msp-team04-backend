CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       kakao_id VARCHAR(255) NOT NULL UNIQUE,
                       parent_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255),
                       phone VARCHAR(50),
                       profile_image VARCHAR(500),
                       refresh_token VARCHAR(500),
                       token_expires_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                       updated_at TIMESTAMP
);

CREATE TABLE child_profile (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               child_name VARCHAR(255) NOT NULL,
                               birth_date DATE NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT now(),
                               updated_at TIMESTAMP,

                               CONSTRAINT fk_child_profile_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
);

CREATE INDEX idx_child_profile_user
    ON child_profile(user_id);

CREATE TABLE child_concern (
                               id BIGSERIAL PRIMARY KEY,
                               child_id BIGINT NOT NULL,
                               concern VARCHAR(255) NOT NULL,

                               CONSTRAINT fk_child_concern_child
                                   FOREIGN KEY (child_id)
                                       REFERENCES child_profile(id),

                               CONSTRAINT uq_child_concern_child_concern
                                   UNIQUE (child_id, concern)
);