CREATE TABLE application (
                             id BIGSERIAL PRIMARY KEY,

                             user_id BIGINT NOT NULL,
                             child_id BIGINT NOT NULL,
                             program_id BIGINT NOT NULL,

                             applicant_name VARCHAR(255) NOT NULL,
                             parent_name VARCHAR(255) NOT NULL,
                             phone VARCHAR(50) NOT NULL,
                             request_note TEXT,
                             ai_start_message TEXT,

                             agree_terms BOOLEAN NOT NULL,
                             agree_privacy BOOLEAN NOT NULL,

                             reserve_no INT,
                             seat_locked_until TIMESTAMP,
                             application_status VARCHAR(50) NOT NULL,

                             applied_at TIMESTAMP NOT NULL DEFAULT now(),
                             updated_at TIMESTAMP,

                             CONSTRAINT fk_application_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES users(id),

                             CONSTRAINT fk_application_child
                                 FOREIGN KEY (child_id)
                                     REFERENCES child_profile(id),

                             CONSTRAINT fk_application_program
                                 FOREIGN KEY (program_id)
                                     REFERENCES program(id),

                             CONSTRAINT chk_application_status
                                 CHECK (
                                     application_status IN ('PENDING', 'PAYMENT_READY', 'CONFIRMED', 'CANCELLED', 'FAILED')
                                     )
);

CREATE INDEX idx_application_user
    ON application(user_id);

CREATE INDEX idx_application_child
    ON application(child_id);

CREATE INDEX idx_application_program
    ON application(program_id);

CREATE INDEX idx_application_status
    ON application(application_status);

CREATE INDEX idx_application_seat_locked_until
    ON application(seat_locked_until);

CREATE INDEX idx_application_user_child_program
    ON application(user_id, child_id, program_id);


CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,

                         application_id BIGINT NOT NULL,

                         payment_method VARCHAR(50) NOT NULL,
                         payment_amount INT NOT NULL DEFAULT 0,
                         payment_status VARCHAR(50) NOT NULL,

                         order_id VARCHAR(255) NOT NULL,
                         payment_key VARCHAR(255),

                         failure_code VARCHAR(100),
                         failure_message TEXT,

                         approved_at TIMESTAMP,
                         cancelled_at TIMESTAMP,
                         created_at TIMESTAMP NOT NULL DEFAULT now(),
                         updated_at TIMESTAMP,

                         CONSTRAINT fk_payment_application
                             FOREIGN KEY (application_id)
                                 REFERENCES application(id),

                         CONSTRAINT uq_payment_application
                             UNIQUE (application_id),

                         CONSTRAINT uq_payment_order_id
                             UNIQUE (order_id),

                         CONSTRAINT chk_payment_method
                             CHECK (
                                 payment_method IN ('TOSS_PAYMENTS', 'FREE')
                                 ),

                         CONSTRAINT chk_payment_status
                             CHECK (
                                 payment_status IN ('READY', 'APPROVED', 'FAILED', 'CANCELLED', 'EXPIRED')
                                 ),

                         CONSTRAINT chk_payment_amount_non_negative
                             CHECK (payment_amount >= 0)
);

CREATE INDEX idx_payment_status
    ON payment(payment_status);


CREATE TABLE review (
                        id BIGSERIAL PRIMARY KEY,

                        program_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        application_id BIGINT NOT NULL,

                        rating NUMERIC(2, 1) NOT NULL,
                        content TEXT,

                        created_at TIMESTAMP NOT NULL DEFAULT now(),
                        updated_at TIMESTAMP,

                        CONSTRAINT fk_review_program
                            FOREIGN KEY (program_id)
                                REFERENCES program(id),

                        CONSTRAINT fk_review_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id),

                        CONSTRAINT fk_review_application
                            FOREIGN KEY (application_id)
                                REFERENCES application(id),

                        CONSTRAINT uq_review_application
                            UNIQUE (application_id),

                        CONSTRAINT chk_review_rating_range
                            CHECK (rating >= 1.0 AND rating <= 5.0)
);

CREATE INDEX idx_review_program
    ON review(program_id);

CREATE INDEX idx_review_user
    ON review(user_id);