-- =========================================================
-- V10__add_indexes_constraints.sql
-- PostgreSQL 전용 Partial Unique Index 추가
-- =========================================================

-- 1. 활성 신청 중복 방지
-- 같은 사용자 + 같은 자녀 + 같은 프로그램에 대해
-- PENDING / PAYMENT_READY / CONFIRMED 상태 신청은 1개만 허용한다.
CREATE UNIQUE INDEX uq_application_active_user_child_program
    ON application (user_id, child_id, program_id)
    WHERE application_status IN ('PENDING', 'PAYMENT_READY', 'CONFIRMED');


-- 2. payment_key는 결제 승인 전에는 NULL 가능
-- 단, 토스페이먼츠에서 payment_key가 발급된 이후에는 중복되면 안 된다.
CREATE UNIQUE INDEX uq_payment_payment_key_not_null
    ON payment (payment_key)
    WHERE payment_key IS NOT NULL;