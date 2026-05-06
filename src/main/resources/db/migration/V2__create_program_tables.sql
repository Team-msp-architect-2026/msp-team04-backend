CREATE TABLE institution (
                             id BIGSERIAL PRIMARY KEY,
                             institution_name VARCHAR(255) NOT NULL,
                             institution_type VARCHAR(50),
                             address VARCHAR(500),
                             phone VARCHAR(50),
                             homepage_url VARCHAR(500),
                             latitude NUMERIC(10, 7),
                             longitude NUMERIC(10, 7),

                             external_source VARCHAR(100),
                             external_id VARCHAR(255),
                             last_synced_at TIMESTAMP,

                             created_at TIMESTAMP NOT NULL DEFAULT now(),
                             updated_at TIMESTAMP,

                             CONSTRAINT uq_institution_external_source_id
                                 UNIQUE (external_source, external_id),

                             CONSTRAINT chk_institution_type
                                 CHECK (
                                     institution_type IS NULL
                                         OR institution_type IN ('PUBLIC', 'PRIVATE', 'ONLINE', 'GOVERNMENT')
                                     ),

                             CONSTRAINT chk_institution_external_pair
                                 CHECK (
                                     (external_source IS NULL AND external_id IS NULL)
                                         OR
                                     (external_source IS NOT NULL AND external_id IS NOT NULL)
                                     )
);

CREATE INDEX idx_institution_name
    ON institution(institution_name);


CREATE TABLE program (
                         id BIGSERIAL PRIMARY KEY,
                         institution_id BIGINT,

                         title VARCHAR(255) NOT NULL,
                         category VARCHAR(50) NOT NULL,
                         description TEXT,
                         program_type VARCHAR(50),

                         target_age_min INT,
                         target_age_max INT,

                         price INT NOT NULL DEFAULT 0,
                         is_free BOOLEAN NOT NULL DEFAULT false,

                         region VARCHAR(100),
                         detail_address VARCHAR(500),
                         latitude NUMERIC(10, 7),
                         longitude NUMERIC(10, 7),

                         operation_start DATE,
                         operation_end DATE,
                         class_time VARCHAR(255),
                         class_type VARCHAR(50),

                         max_capacity INT,
                         remain_capacity INT,

                         is_recruiting BOOLEAN NOT NULL DEFAULT true,
                         deadline_date DATE,

                         rating_avg NUMERIC(3, 2) NOT NULL DEFAULT 0,
                         review_count INT NOT NULL DEFAULT 0,

                         is_public BOOLEAN NOT NULL DEFAULT true,
                         image_url VARCHAR(500),
                         curriculum TEXT,
                         contact_phone VARCHAR(50),
                         contact_url VARCHAR(500),

                         external_source VARCHAR(100),
                         external_id VARCHAR(255),
                         last_synced_at TIMESTAMP,

                         created_at TIMESTAMP NOT NULL DEFAULT now(),
                         updated_at TIMESTAMP,

                         CONSTRAINT fk_program_institution
                             FOREIGN KEY (institution_id)
                                 REFERENCES institution(id),

                         CONSTRAINT uq_program_external_source_id
                             UNIQUE (external_source, external_id),

                         CONSTRAINT chk_program_category
                             CHECK (
                                 category IN ('EDUCATION', 'CARE', 'EXPERIENCE', 'SPORTS', 'ART', 'LANGUAGE', 'ETC')
                                 ),

                         CONSTRAINT chk_program_type
                             CHECK (
                                 program_type IS NULL
                                     OR program_type IN ('PUBLIC', 'PRIVATE', 'ONLINE', 'GOVERNMENT')
                                 ),

                         CONSTRAINT chk_program_class_type
                             CHECK (
                                 class_type IS NULL
                                     OR class_type IN ('SMALL', 'MEDIUM', 'ONE_ON_ONE', 'ONLINE', 'VISIT')
                                 ),

                         CONSTRAINT chk_program_price_non_negative
                             CHECK (price >= 0),

                         CONSTRAINT chk_program_max_capacity_non_negative
                             CHECK (max_capacity IS NULL OR max_capacity >= 0),

                         CONSTRAINT chk_program_remain_capacity_non_negative
                             CHECK (remain_capacity IS NULL OR remain_capacity >= 0),

                         CONSTRAINT chk_program_remain_capacity_lte_max_capacity
                             CHECK (
                                 max_capacity IS NULL
                                     OR remain_capacity IS NULL
                                     OR remain_capacity <= max_capacity
                                 ),

                         CONSTRAINT chk_program_rating_avg_range
                             CHECK (rating_avg >= 0 AND rating_avg <= 5),

                         CONSTRAINT chk_program_review_count_non_negative
                             CHECK (review_count >= 0),

                         CONSTRAINT chk_program_external_pair
                             CHECK (
                                 (external_source IS NULL AND external_id IS NULL)
                                     OR
                                 (external_source IS NOT NULL AND external_id IS NOT NULL)
                                 )
);

CREATE INDEX idx_program_institution
    ON program(institution_id);

CREATE INDEX idx_program_category
    ON program(category);

CREATE INDEX idx_program_region
    ON program(region);

CREATE INDEX idx_program_is_recruiting
    ON program(is_recruiting);

CREATE INDEX idx_program_deadline_date
    ON program(deadline_date);


CREATE TABLE program_tag (
                             id BIGSERIAL PRIMARY KEY,
                             program_id BIGINT NOT NULL,
                             tag VARCHAR(255) NOT NULL,

                             CONSTRAINT fk_program_tag_program
                                 FOREIGN KEY (program_id)
                                     REFERENCES program(id),

                             CONSTRAINT uq_program_tag_program_tag
                                 UNIQUE (program_id, tag)
);