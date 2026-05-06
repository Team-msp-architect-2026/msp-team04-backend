CREATE TABLE community_post (
                                id BIGSERIAL PRIMARY KEY,

                                user_id BIGINT NOT NULL,

                                category VARCHAR(50) NOT NULL,
                                child_age VARCHAR(50),
                                title VARCHAR(255) NOT NULL,
                                content TEXT NOT NULL,
                                image_url VARCHAR(500),

                                comment_count INT NOT NULL DEFAULT 0,
                                like_count INT NOT NULL DEFAULT 0,

                                created_at TIMESTAMP NOT NULL DEFAULT now(),
                                updated_at TIMESTAMP,

                                CONSTRAINT fk_community_post_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id),

                                CONSTRAINT chk_community_post_category
                                    CHECK (
                                        category IN ('REVIEW', 'QUESTION', 'INFO', 'EDUCATION', 'CARE')
                                        ),

                                CONSTRAINT chk_community_post_comment_count
                                    CHECK (comment_count >= 0),

                                CONSTRAINT chk_community_post_like_count
                                    CHECK (like_count >= 0)
);

CREATE INDEX idx_community_post_user
    ON community_post(user_id);

CREATE INDEX idx_community_post_category
    ON community_post(category);

CREATE INDEX idx_community_post_created_at
    ON community_post(created_at);


CREATE TABLE community_comment (
                                   id BIGSERIAL PRIMARY KEY,

                                   post_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,

                                   content TEXT NOT NULL,
                                   like_count INT NOT NULL DEFAULT 0,

                                   created_at TIMESTAMP NOT NULL DEFAULT now(),
                                   updated_at TIMESTAMP,

                                   CONSTRAINT fk_community_comment_post
                                       FOREIGN KEY (post_id)
                                           REFERENCES community_post(id),

                                   CONSTRAINT fk_community_comment_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES users(id),

                                   CONSTRAINT chk_community_comment_like_count
                                       CHECK (like_count >= 0)
);

CREATE INDEX idx_community_comment_post
    ON community_comment(post_id);

CREATE INDEX idx_community_comment_user
    ON community_comment(user_id);


CREATE TABLE post_tag (
                          id BIGSERIAL PRIMARY KEY,

                          post_id BIGINT NOT NULL,
                          tag VARCHAR(255) NOT NULL,

                          CONSTRAINT fk_post_tag_post
                              FOREIGN KEY (post_id)
                                  REFERENCES community_post(id),

                          CONSTRAINT uq_post_tag_post_tag
                              UNIQUE (post_id, tag)
);


CREATE TABLE post_like (
                           id BIGSERIAL PRIMARY KEY,

                           post_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,

                           created_at TIMESTAMP NOT NULL DEFAULT now(),

                           CONSTRAINT fk_post_like_post
                               FOREIGN KEY (post_id)
                                   REFERENCES community_post(id),

                           CONSTRAINT fk_post_like_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id),

                           CONSTRAINT uq_post_like_post_user
                               UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_like_user
    ON post_like(user_id);


CREATE TABLE comment_like (
                              id BIGSERIAL PRIMARY KEY,

                              comment_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,

                              created_at TIMESTAMP NOT NULL DEFAULT now(),

                              CONSTRAINT fk_comment_like_comment
                                  FOREIGN KEY (comment_id)
                                      REFERENCES community_comment(id),

                              CONSTRAINT fk_comment_like_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id),

                              CONSTRAINT uq_comment_like_comment_user
                                  UNIQUE (comment_id, user_id)
);

CREATE INDEX idx_comment_like_user
    ON comment_like(user_id);