CREATE TABLE recommendation_preference_concerns (
    preference_id BIGINT NOT NULL,
    concern VARCHAR(255) NOT NULL,
    CONSTRAINT fk_rpc_preference
        FOREIGN KEY (preference_id)
        REFERENCES recommendation_preference(id)
);

CREATE TABLE recommendation_preference_subject_details (
    preference_id BIGINT NOT NULL,
    subject_detail VARCHAR(255) NOT NULL,
    CONSTRAINT fk_rpsd_preference
        FOREIGN KEY (preference_id)
        REFERENCES recommendation_preference(id)
);
