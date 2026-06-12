ALTER TABLE benefit_match
    DROP CONSTRAINT IF EXISTS chk_benefit_match_status;

ALTER TABLE benefit_match
    ADD CONSTRAINT chk_benefit_match_status
        CHECK (
            match_status IN ('APPLICABLE', 'CONDITION_CHECK', 'NOT_ELIGIBLE')
        );

CREATE INDEX IF NOT EXISTS idx_benefit_match_child_status
    ON benefit_match(child_id, match_status);
