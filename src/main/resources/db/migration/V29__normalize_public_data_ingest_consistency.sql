-- =========================================================
-- V29__normalize_public_data_ingest_consistency.sql
-- Public-data ingest consistency normalization
-- =========================================================
-- Goals:
-- 1. Backfill program.institution_id for real public-data rows.
-- 2. Normalize existing Seoul academy program titles so app cards do not show only course labels.
-- 3. Remove only known local/dev sample catalog rows by exact seed key patterns.
-- 4. Keep user/child seed data for local login/dev flows.

-- =========================================================
-- 1. Backfill real public-data program -> institution relation
-- =========================================================
UPDATE program p
SET institution_id = i.id,
    updated_at = NOW()
FROM institution i
WHERE p.external_source IN ('SEOUL_ACADEMY', 'SEOUL_CARE', 'SEOUL_PUBLIC_PROGRAM')
  AND i.external_source = p.external_source
  AND i.external_id = CONCAT('INST_', p.external_id)
  AND (p.institution_id IS NULL OR p.institution_id <> i.id);

DO $$
DECLARE
    remaining_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO remaining_count
    FROM program p
    WHERE p.external_source IN ('SEOUL_ACADEMY', 'SEOUL_CARE', 'SEOUL_PUBLIC_PROGRAM')
      AND p.institution_id IS NULL
      AND EXISTS (
          SELECT 1
          FROM institution i
          WHERE i.external_source = p.external_source
            AND i.external_id = CONCAT('INST_', p.external_id)
      );

    IF remaining_count > 0 THEN
        RAISE EXCEPTION 'public-data program institution backfill failed. remaining_count=%', remaining_count;
    END IF;
END $$;

-- =========================================================
-- 2. Normalize existing Seoul academy program title
-- =========================================================
UPDATE program p
SET title = CASE
        WHEN NULLIF(TRIM(p.title), '') IS NULL THEN i.institution_name
        WHEN p.title LIKE i.institution_name || ' - %' THEN p.title
        ELSE i.institution_name || ' - ' || p.title
    END,
    curriculum = COALESCE(NULLIF(TRIM(p.curriculum), ''), NULLIF(TRIM(p.title), '')),
    updated_at = NOW()
FROM institution i
WHERE p.external_source = 'SEOUL_ACADEMY'
  AND i.external_source = 'SEOUL_ACADEMY'
  AND i.id = p.institution_id;

-- =========================================================
-- 3. Remove only known local/dev sample catalog rows
-- =========================================================
CREATE TEMP TABLE tmp_sample_program_ids ON COMMIT DROP AS
SELECT id
FROM program
WHERE
    (external_source = 'MANUAL' AND external_id ~ '^PRG-[0-9]{3}$')
    OR
    (external_source = 'CSV' AND external_id ~ '^CSV-PRG-[0-9]{3}$');

CREATE TEMP TABLE tmp_sample_application_ids ON COMMIT DROP AS
SELECT id
FROM application
WHERE program_id IN (SELECT id FROM tmp_sample_program_ids);

CREATE TEMP TABLE tmp_sample_benefit_ids ON COMMIT DROP AS
SELECT id
FROM benefit_master
WHERE
    (external_source = 'MANUAL' AND external_id ~ '^BEN-[0-9]{3}$')
    OR
    (external_source = 'CSV' AND external_id ~ '^CSV-BEN-[0-9]{3}$');

DELETE FROM payment
WHERE application_id IN (SELECT id FROM tmp_sample_application_ids);

DELETE FROM review
WHERE application_id IN (SELECT id FROM tmp_sample_application_ids)
   OR program_id IN (SELECT id FROM tmp_sample_program_ids);

DELETE FROM ai_recommendation
WHERE program_id IN (SELECT id FROM tmp_sample_program_ids);

DELETE FROM bookmark
WHERE program_id IN (SELECT id FROM tmp_sample_program_ids);

DELETE FROM program_tag
WHERE program_id IN (SELECT id FROM tmp_sample_program_ids);

DELETE FROM application
WHERE id IN (SELECT id FROM tmp_sample_application_ids);

DELETE FROM benefit_match
WHERE benefit_id IN (SELECT id FROM tmp_sample_benefit_ids);

DELETE FROM program
WHERE id IN (SELECT id FROM tmp_sample_program_ids);

DELETE FROM benefit_master
WHERE id IN (SELECT id FROM tmp_sample_benefit_ids);

DELETE FROM institution i
WHERE
    (
        (i.external_source = 'MANUAL' AND i.external_id ~ '^INST-[0-9]{3}$')
        OR
        (i.external_source IN ('PUBLIC', 'PRIVATE') AND i.external_id ~ '^INST[0-9]{3}$')
    )
  AND NOT EXISTS (
      SELECT 1
      FROM program p
      WHERE p.institution_id = i.id
  );

DELETE FROM ai_report
WHERE id IN (1, 2, 3)
  AND child_id IN (1, 2, 3);
