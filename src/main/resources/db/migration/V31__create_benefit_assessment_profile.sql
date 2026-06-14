CREATE TABLE benefit_assessment_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    region VARCHAR(50) NOT NULL,
    district VARCHAR(50),
    household_size INT NOT NULL,
    monthly_income_range VARCHAR(30) NOT NULL,
    caregiver_age_range VARCHAR(30) NOT NULL,
    dual_income BOOLEAN NOT NULL DEFAULT FALSE,
    single_parent BOOLEAN NOT NULL DEFAULT FALSE,
    multi_child_family BOOLEAN NOT NULL DEFAULT FALSE,
    multicultural_family BOOLEAN NOT NULL DEFAULT FALSE,
    disabled_family_member BOOLEAN NOT NULL DEFAULT FALSE,
    unknown_income BOOLEAN NOT NULL DEFAULT FALSE,
    consent_agreed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_benefit_assessment_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_benefit_assessment_profile_user
        UNIQUE (user_id),

    CONSTRAINT chk_benefit_assessment_household_size
        CHECK (household_size BETWEEN 1 AND 20),

    CONSTRAINT chk_benefit_assessment_monthly_income_range
        CHECK (
            monthly_income_range IN (
                'UNKNOWN',
                'UNDER_200',
                'RANGE_200_350',
                'RANGE_350_500',
                'RANGE_500_700',
                'OVER_700'
            )
        ),

    CONSTRAINT chk_benefit_assessment_caregiver_age_range
        CHECK (
            caregiver_age_range IN (
                'UNKNOWN',
                'UNDER_30',
                'RANGE_30_39',
                'RANGE_40_49',
                'RANGE_50_59',
                'OVER_60'
            )
        ),

    CONSTRAINT chk_benefit_assessment_consent_agreed
        CHECK (consent_agreed = TRUE)
);

CREATE INDEX idx_benefit_assessment_profile_region_district
    ON benefit_assessment_profile(region, district);

CREATE INDEX idx_benefit_assessment_profile_income
    ON benefit_assessment_profile(monthly_income_range);
