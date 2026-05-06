-- =========================================================
-- V20__insert_seed_data.sql
-- MoMent local/dev seed data
-- =========================================================

-- =========================================================
-- 1. Users
-- =========================================================
INSERT INTO users (
    id, kakao_id, parent_name, email, phone, profile_image, refresh_token, token_expires_at, created_at, updated_at
) VALUES
      (1, 'kakao_seed_001', '김민지', 'minji@example.com', '010-1111-2222', NULL, NULL, NULL, now(), now()),
      (2, 'kakao_seed_002', '정아름', 'areum@example.com', '010-3333-4444', NULL, NULL, NULL, now(), now()),
      (3, 'kakao_seed_003', '박서연', 'seoyeon@example.com', '010-5555-6666', NULL, NULL, NULL, now(), now())
    ON CONFLICT (id) DO NOTHING;

-- =========================================================
-- 2. Child Profile / Concern
-- =========================================================
INSERT INTO child_profile (
    id, user_id, child_name, birth_date, created_at, updated_at
) VALUES
      (1, 1, '민준', '2018-05-12', now(), now()),
      (2, 1, '서아', '2020-09-03', now(), now()),
      (3, 2, '지후', '2016-11-21', now(), now()),
      (4, 3, '하린', '2019-02-14', now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO child_concern (
    id, child_id, concern
) VALUES
      (1, 1, '기초학습'),
      (2, 1, '수학'),
      (3, 1, '코딩'),
      (4, 2, '미술'),
      (5, 2, '사회성'),
      (6, 3, '영어'),
      (7, 3, '체육'),
      (8, 4, '독서'),
      (9, 4, '창의력')
    ON CONFLICT (child_id, concern) DO NOTHING;

-- =========================================================
-- 3. Institution 10건
-- =========================================================
INSERT INTO institution (
    id, institution_name, institution_type, address, phone, homepage_url,
    latitude, longitude, external_source, external_id, last_synced_at, created_at, updated_at
) VALUES
      (1, '강남구 공공돌봄센터', 'PUBLIC', '서울특별시 강남구 테헤란로 100', '02-111-1000', 'https://example.com/inst/1', 37.497942, 127.027621, 'MANUAL', 'INST-001', now(), now(), now()),
      (2, '송파 어린이문화센터', 'PUBLIC', '서울특별시 송파구 올림픽로 200', '02-111-2000', 'https://example.com/inst/2', 37.514575, 127.105399, 'MANUAL', 'INST-002', now(), now(), now()),
      (3, '마포 창의학습관', 'PUBLIC', '서울특별시 마포구 월드컵로 50', '02-111-3000', 'https://example.com/inst/3', 37.566200, 126.901900, 'MANUAL', 'INST-003', now(), now(), now()),
      (4, '노원 방과후배움터', 'PUBLIC', '서울특별시 노원구 노해로 120', '02-111-4000', 'https://example.com/inst/4', 37.654259, 127.056294, 'MANUAL', 'INST-004', now(), now(), now()),
      (5, '서초 키즈아트센터', 'PRIVATE', '서울특별시 서초구 서초대로 77', '02-111-5000', 'https://example.com/inst/5', 37.483712, 127.032411, 'MANUAL', 'INST-005', now(), now(), now()),
      (6, '성동 스포츠클럽', 'PRIVATE', '서울특별시 성동구 왕십리로 88', '02-111-6000', 'https://example.com/inst/6', 37.563341, 127.037102, 'MANUAL', 'INST-006', now(), now(), now()),
      (7, '은평 가족지원센터', 'GOVERNMENT', '서울특별시 은평구 은평로 195', '02-111-7000', 'https://example.com/inst/7', 37.602709, 126.929111, 'MANUAL', 'INST-007', now(), now(), now()),
      (8, '구로 디지털러닝센터', 'PUBLIC', '서울특별시 구로구 디지털로 300', '02-111-8000', 'https://example.com/inst/8', 37.485215, 126.901594, 'MANUAL', 'INST-008', now(), now(), now()),
      (9, '중랑 우리동네돌봄', 'PUBLIC', '서울특별시 중랑구 망우로 350', '02-111-9000', 'https://example.com/inst/9', 37.606324, 127.092623, 'MANUAL', 'INST-009', now(), now(), now()),
      (10, '온라인 에듀케어랩', 'ONLINE', '온라인', '02-111-1010', 'https://example.com/inst/10', 37.566500, 126.978000, 'MANUAL', 'INST-010', now(), now(), now())
    ON CONFLICT (id) DO NOTHING;

-- =========================================================
-- 4. Program 50건
-- =========================================================
INSERT INTO program (
    id, institution_id, title, category, description, program_type,
    target_age_min, target_age_max, price, is_free,
    region, detail_address, latitude, longitude,
    operation_start, operation_end, class_time, class_type,
    max_capacity, remain_capacity, is_recruiting, deadline_date,
    rating_avg, review_count, is_public, image_url, curriculum, contact_phone, contact_url,
    external_source, external_id, last_synced_at, created_at, updated_at
) VALUES
      (1, 1, '초등 수학 기초반', 'EDUCATION', '초등 저학년 수학 개념을 쉽게 익히는 수업', 'PUBLIC', 6, 9, 0, true, '강남구', '강남구 테헤란로 100', 37.497942, 127.027621, '2026-05-20', '2026-07-20', '화 16:00-17:00', 'SMALL', 20, 20, true, '2026-05-18', 4.7, 12, true, NULL, '수 개념, 연산, 문제풀이', '02-111-1000', 'https://example.com/program/1', 'MANUAL', 'PRG-001', now(), now(), now()),
      (2, 1, '방과후 독서 습관 만들기', 'EDUCATION', '책 읽기와 감상 표현을 배우는 독서 프로그램', 'PUBLIC', 7, 10, 0, true, '강남구', '강남구 테헤란로 100', 37.497942, 127.027621, '2026-05-25', '2026-07-25', '목 16:00-17:30', 'SMALL', 15, 14, true, '2026-05-22', 4.6, 9, true, NULL, '독서, 발표, 감상문', '02-111-1000', 'https://example.com/program/2', 'MANUAL', 'PRG-002', now(), now(), now()),
      (3, 1, '놀이로 배우는 과학실험', 'EXPERIENCE', '생활 속 과학 원리를 실험으로 배우는 활동', 'PUBLIC', 6, 10, 10000, false, '강남구', '강남구 테헤란로 100', 37.497942, 127.027621, '2026-06-01', '2026-08-01', '토 10:00-11:30', 'SMALL', 18, 16, true, '2026-05-29', 4.8, 15, true, NULL, '과학실험, 관찰, 기록', '02-111-1000', 'https://example.com/program/3', 'MANUAL', 'PRG-003', now(), now(), now()),
      (4, 1, '초등 영어 파닉스', 'LANGUAGE', '기초 영어 발음과 단어 읽기를 배우는 수업', 'PRIVATE', 6, 9, 80000, false, '강남구', '강남구 테헤란로 100', 37.497942, 127.027621, '2026-06-03', '2026-08-03', '수 17:00-18:00', 'SMALL', 12, 10, true, '2026-05-31', 4.3, 8, true, NULL, '파닉스, 단어, 짧은 문장', '02-111-1000', 'https://example.com/program/4', 'MANUAL', 'PRG-004', now(), now(), now()),
      (5, 1, '여름방학 돌봄교실', 'CARE', '방학 중 돌봄 공백을 줄이는 공공 돌봄 프로그램', 'PUBLIC', 6, 11, 0, true, '강남구', '강남구 테헤란로 100', 37.497942, 127.027621, '2026-07-01', '2026-08-20', '월-금 09:00-13:00', 'MEDIUM', 30, 25, true, '2026-06-20', 4.5, 20, true, NULL, '돌봄, 놀이, 숙제지도', '02-111-1000', 'https://example.com/program/5', 'MANUAL', 'PRG-005', now(), now(), now()),

      (6, 2, '송파 어린이 뮤지컬', 'ART', '노래와 움직임으로 표현력을 기르는 수업', 'PUBLIC', 7, 12, 20000, false, '송파구', '송파구 올림픽로 200', 37.514575, 127.105399, '2026-05-22', '2026-07-22', '금 16:00-18:00', 'MEDIUM', 25, 22, true, '2026-05-19', 4.9, 18, true, NULL, '노래, 연기, 무대표현', '02-111-2000', 'https://example.com/program/6', 'MANUAL', 'PRG-006', now(), now(), now()),
      (7, 2, '창의 미술 탐험', 'ART', '다양한 재료로 창의적 표현을 경험하는 미술 수업', 'PUBLIC', 5, 9, 0, true, '송파구', '송파구 올림픽로 200', 37.514575, 127.105399, '2026-05-28', '2026-07-28', '화 15:30-16:30', 'SMALL', 16, 13, true, '2026-05-25', 4.6, 11, true, NULL, '색채, 만들기, 전시', '02-111-2000', 'https://example.com/program/7', 'MANUAL', 'PRG-007', now(), now(), now()),
      (8, 2, '초등 축구교실', 'SPORTS', '기초 체력과 협동심을 기르는 축구 수업', 'PUBLIC', 7, 13, 30000, false, '송파구', '송파구 올림픽로 200', 37.514575, 127.105399, '2026-06-02', '2026-08-02', '토 09:00-10:30', 'MEDIUM', 24, 20, true, '2026-05-30', 4.4, 10, true, NULL, '기초 드리블, 패스, 경기', '02-111-2000', 'https://example.com/program/8', 'MANUAL', 'PRG-008', now(), now(), now()),
      (9, 2, '유아 감각놀이', 'EXPERIENCE', '오감 발달을 돕는 놀이 중심 활동', 'PUBLIC', 3, 5, 0, true, '송파구', '송파구 올림픽로 200', 37.514575, 127.105399, '2026-06-05', '2026-07-05', '목 11:00-12:00', 'SMALL', 12, 11, true, '2026-06-01', 4.7, 7, true, NULL, '감각놀이, 소근육, 사회성', '02-111-2000', 'https://example.com/program/9', 'MANUAL', 'PRG-009', now(), now(), now()),
      (10, 2, '기초 한글 읽기', 'EDUCATION', '예비초등과 저학년을 위한 한글 읽기 수업', 'PUBLIC', 5, 8, 10000, false, '송파구', '송파구 올림픽로 200', 37.514575, 127.105399, '2026-06-10', '2026-08-10', '월 16:00-17:00', 'SMALL', 15, 15, true, '2026-06-05', 4.2, 6, true, NULL, '한글 읽기, 문장 이해', '02-111-2000', 'https://example.com/program/10', 'MANUAL', 'PRG-010', now(), now(), now()),

      (11, 3, '마포 코딩 첫걸음', 'EDUCATION', '블록 코딩으로 알고리즘 기초를 배우는 수업', 'PUBLIC', 8, 13, 0, true, '마포구', '마포구 월드컵로 50', 37.566200, 126.901900, '2026-05-24', '2026-07-24', '수 16:30-18:00', 'SMALL', 20, 18, true, '2026-05-20', 4.8, 22, true, NULL, '블록코딩, 순서, 반복', '02-111-3000', 'https://example.com/program/11', 'MANUAL', 'PRG-011', now(), now(), now()),
      (12, 3, '논술과 글쓰기', 'EDUCATION', '생각을 글로 정리하는 초등 논술 수업', 'PUBLIC', 9, 13, 20000, false, '마포구', '마포구 월드컵로 50', 37.566200, 126.901900, '2026-05-27', '2026-07-27', '금 17:00-18:30', 'SMALL', 14, 12, true, '2026-05-23', 4.5, 13, true, NULL, '논술, 주장, 근거', '02-111-3000', 'https://example.com/program/12', 'MANUAL', 'PRG-012', now(), now(), now()),
      (13, 3, '창의 보드게임 수학', 'EDUCATION', '보드게임으로 수학적 사고를 키우는 수업', 'PUBLIC', 7, 11, 0, true, '마포구', '마포구 월드컵로 50', 37.566200, 126.901900, '2026-06-01', '2026-08-01', '토 13:00-14:30', 'SMALL', 16, 14, true, '2026-05-28', 4.6, 10, true, NULL, '전략, 수 감각, 문제해결', '02-111-3000', 'https://example.com/program/13', 'MANUAL', 'PRG-013', now(), now(), now()),
      (14, 3, '역사 이야기 교실', 'EDUCATION', '초등 눈높이로 배우는 한국사 이야기 수업', 'PUBLIC', 9, 13, 30000, false, '마포구', '마포구 월드컵로 50', 37.566200, 126.901900, '2026-06-08', '2026-08-08', '화 17:00-18:30', 'MEDIUM', 20, 19, true, '2026-06-04', 4.1, 5, true, NULL, '한국사, 인물, 토론', '02-111-3000', 'https://example.com/program/14', 'MANUAL', 'PRG-014', now(), now(), now()),
      (15, 3, '마포 방과후 돌봄', 'CARE', '학기 중 하교 후 돌봄을 제공하는 프로그램', 'PUBLIC', 6, 10, 0, true, '마포구', '마포구 월드컵로 50', 37.566200, 126.901900, '2026-05-20', '2026-12-20', '월-금 14:00-18:00', 'MEDIUM', 35, 30, true, '2026-05-18', 4.4, 17, true, NULL, '돌봄, 숙제지도, 간식', '02-111-3000', 'https://example.com/program/15', 'MANUAL', 'PRG-015', now(), now(), now()),

      (16, 4, '노원 과학탐구반', 'EXPERIENCE', '실험과 관찰로 배우는 탐구 수업', 'PUBLIC', 8, 12, 0, true, '노원구', '노원구 노해로 120', 37.654259, 127.056294, '2026-05-23', '2026-07-23', '목 16:30-18:00', 'SMALL', 18, 16, true, '2026-05-20', 4.7, 14, true, NULL, '실험, 탐구보고서', '02-111-4000', 'https://example.com/program/16', 'MANUAL', 'PRG-016', now(), now(), now()),
      (17, 4, '초등 영어 회화', 'LANGUAGE', '상황별 영어 표현을 말해보는 회화 수업', 'PRIVATE', 8, 13, 90000, false, '노원구', '노원구 노해로 120', 37.654259, 127.056294, '2026-06-01', '2026-08-01', '수 17:00-18:00', 'SMALL', 12, 8, true, '2026-05-29', 4.3, 9, true, NULL, '회화, 발음, 역할놀이', '02-111-4000', 'https://example.com/program/17', 'MANUAL', 'PRG-017', now(), now(), now()),
      (18, 4, '태권도 기초 체험', 'SPORTS', '기초 체력과 예절을 배우는 태권도 체험', 'PRIVATE', 6, 10, 50000, false, '노원구', '노원구 노해로 120', 37.654259, 127.056294, '2026-06-05', '2026-07-05', '금 16:00-17:00', 'MEDIUM', 25, 21, true, '2026-06-01', 4.4, 11, true, NULL, '기본동작, 예절, 체력', '02-111-4000', 'https://example.com/program/18', 'MANUAL', 'PRG-018', now(), now(), now()),
      (19, 4, '초등 자기주도학습', 'EDUCATION', '학습 계획과 습관을 잡는 프로그램', 'PUBLIC', 9, 13, 0, true, '노원구', '노원구 노해로 120', 37.654259, 127.056294, '2026-06-11', '2026-08-11', '월 17:00-18:30', 'SMALL', 15, 13, true, '2026-06-07', 4.6, 8, true, NULL, '계획, 복습, 목표관리', '02-111-4000', 'https://example.com/program/19', 'MANUAL', 'PRG-019', now(), now(), now()),
      (20, 4, '노원 무료 돌봄 캠프', 'CARE', '초등 방학 돌봄과 체험활동을 함께 제공', 'PUBLIC', 6, 12, 0, true, '노원구', '노원구 노해로 120', 37.654259, 127.056294, '2026-07-10', '2026-08-18', '월-금 09:00-15:00', 'MEDIUM', 40, 35, true, '2026-06-30', 4.7, 21, true, NULL, '돌봄, 체험, 독서', '02-111-4000', 'https://example.com/program/20', 'MANUAL', 'PRG-020', now(), now(), now()),

      (21, 5, '서초 수채화 클래스', 'ART', '수채화 기초와 표현 기법을 배우는 수업', 'PRIVATE', 7, 12, 120000, false, '서초구', '서초구 서초대로 77', 37.483712, 127.032411, '2026-05-21', '2026-07-21', '화 17:00-18:30', 'SMALL', 10, 7, true, '2026-05-18', 4.8, 12, true, NULL, '스케치, 색칠, 작품완성', '02-111-5000', 'https://example.com/program/21', 'MANUAL', 'PRG-021', now(), now(), now()),
      (22, 5, '키즈 도예 체험', 'ART', '흙으로 작품을 만드는 감각 체험 수업', 'PRIVATE', 6, 11, 60000, false, '서초구', '서초구 서초대로 77', 37.483712, 127.032411, '2026-05-26', '2026-07-26', '토 10:00-12:00', 'SMALL', 12, 10, true, '2026-05-23', 4.5, 10, true, NULL, '도예, 만들기, 굽기', '02-111-5000', 'https://example.com/program/22', 'MANUAL', 'PRG-022', now(), now(), now()),
      (23, 5, '창의 음악놀이', 'ART', '리듬과 악기로 표현력을 키우는 음악 수업', 'PRIVATE', 4, 7, 70000, false, '서초구', '서초구 서초대로 77', 37.483712, 127.032411, '2026-06-01', '2026-08-01', '수 15:00-16:00', 'SMALL', 12, 12, true, '2026-05-28', 4.2, 7, true, NULL, '리듬, 악기, 노래', '02-111-5000', 'https://example.com/program/23', 'MANUAL', 'PRG-023', now(), now(), now()),
      (24, 5, '서초 영어 그림책', 'LANGUAGE', '영어 그림책으로 듣기와 표현을 익히는 수업', 'PRIVATE', 5, 8, 90000, false, '서초구', '서초구 서초대로 77', 37.483712, 127.032411, '2026-06-04', '2026-08-04', '목 16:00-17:00', 'SMALL', 10, 9, true, '2026-06-01', 4.4, 6, true, NULL, '영어책, 듣기, 말하기', '02-111-5000', 'https://example.com/program/24', 'MANUAL', 'PRG-024', now(), now(), now()),
      (25, 5, '서초 미술 포트폴리오', 'ART', '고학년 대상 작품집 제작 수업', 'PRIVATE', 10, 13, 180000, false, '서초구', '서초구 서초대로 77', 37.483712, 127.032411, '2026-06-12', '2026-08-12', '금 18:00-19:30', 'ONE_ON_ONE', 8, 5, true, '2026-06-08', 4.9, 9, true, NULL, '드로잉, 구성, 포트폴리오', '02-111-5000', 'https://example.com/program/25', 'MANUAL', 'PRG-025', now(), now(), now()),

      (26, 6, '성동 어린이 농구', 'SPORTS', '농구 기본기와 협동심을 키우는 수업', 'PRIVATE', 8, 13, 70000, false, '성동구', '성동구 왕십리로 88', 37.563341, 127.037102, '2026-05-23', '2026-07-23', '토 09:00-10:30', 'MEDIUM', 20, 18, true, '2026-05-20', 4.4, 11, true, NULL, '드리블, 슛, 경기', '02-111-6000', 'https://example.com/program/26', 'MANUAL', 'PRG-026', now(), now(), now()),
      (27, 6, '성동 키즈 요가', 'SPORTS', '자세와 호흡을 배우는 어린이 요가', 'PRIVATE', 6, 10, 50000, false, '성동구', '성동구 왕십리로 88', 37.563341, 127.037102, '2026-06-01', '2026-07-15', '월 16:00-17:00', 'SMALL', 12, 11, true, '2026-05-28', 4.6, 8, true, NULL, '스트레칭, 호흡, 균형', '02-111-6000', 'https://example.com/program/27', 'MANUAL', 'PRG-027', now(), now(), now()),
      (28, 6, '초등 줄넘기 마스터', 'SPORTS', '기초 체력과 리듬감을 높이는 줄넘기 수업', 'PRIVATE', 6, 12, 40000, false, '성동구', '성동구 왕십리로 88', 37.563341, 127.037102, '2026-06-05', '2026-07-20', '수 16:00-17:00', 'MEDIUM', 18, 15, true, '2026-06-02', 4.3, 6, true, NULL, '기본 점프, 리듬, 체력', '02-111-6000', 'https://example.com/program/28', 'MANUAL', 'PRG-028', now(), now(), now()),
      (29, 6, '놀이체육 교실', 'SPORTS', '게임형 활동으로 운동 습관을 만드는 수업', 'PRIVATE', 4, 8, 30000, false, '성동구', '성동구 왕십리로 88', 37.563341, 127.037102, '2026-06-08', '2026-07-08', '금 15:00-16:00', 'MEDIUM', 20, 19, true, '2026-06-04', 4.1, 5, true, NULL, '놀이, 균형, 협동', '02-111-6000', 'https://example.com/program/29', 'MANUAL', 'PRG-029', now(), now(), now()),
      (30, 6, '성동 방학 스포츠캠프', 'SPORTS', '방학 중 다양한 운동을 경험하는 캠프', 'PRIVATE', 8, 13, 150000, false, '성동구', '성동구 왕십리로 88', 37.563341, 127.037102, '2026-07-15', '2026-08-15', '월-금 10:00-12:00', 'MEDIUM', 30, 28, true, '2026-07-01', 4.7, 16, true, NULL, '농구, 축구, 줄넘기', '02-111-6000', 'https://example.com/program/30', 'MANUAL', 'PRG-030', now(), now(), now()),

      (31, 7, '은평 조부모 돌봄 안내', 'CARE', '가족 돌봄 지원과 신청 방법을 안내하는 프로그램', 'GOVERNMENT', 3, 13, 0, true, '은평구', '은평구 은평로 195', 37.602709, 126.929111, '2026-05-20', '2026-06-20', '수 14:00-15:00', 'MEDIUM', 30, 26, true, '2026-05-18', 4.5, 9, true, NULL, '돌봄지원, 신청방법, 상담', '02-111-7000', 'https://example.com/program/31', 'MANUAL', 'PRG-031', now(), now(), now()),
      (32, 7, '은평 무료 부모교육', 'CARE', '육아 정보와 양육 스트레스 관리를 배우는 부모교육', 'GOVERNMENT', 3, 13, 0, true, '은평구', '은평구 은평로 195', 37.602709, 126.929111, '2026-06-01', '2026-06-30', '목 10:00-12:00', 'MEDIUM', 40, 35, true, '2026-05-29', 4.6, 12, true, NULL, '양육정보, 스트레스관리', '02-111-7000', 'https://example.com/program/32', 'MANUAL', 'PRG-032', now(), now(), now()),
      (33, 7, '가족 놀이 상담', 'CARE', '부모와 아이가 함께하는 놀이 상담 프로그램', 'GOVERNMENT', 3, 8, 0, true, '은평구', '은평구 은평로 195', 37.602709, 126.929111, '2026-06-07', '2026-07-07', '토 10:00-11:30', 'SMALL', 10, 8, true, '2026-06-03', 4.7, 8, true, NULL, '놀이상담, 부모코칭', '02-111-7000', 'https://example.com/program/33', 'MANUAL', 'PRG-033', now(), now(), now()),
      (34, 7, '초등 정서지원 그룹', 'CARE', '또래 관계와 감정 표현을 돕는 그룹 프로그램', 'GOVERNMENT', 7, 12, 0, true, '은평구', '은평구 은평로 195', 37.602709, 126.929111, '2026-06-12', '2026-07-12', '금 16:00-17:30', 'SMALL', 12, 10, true, '2026-06-08', 4.3, 6, true, NULL, '감정표현, 또래관계', '02-111-7000', 'https://example.com/program/34', 'MANUAL', 'PRG-034', now(), now(), now()),
      (35, 7, '은평 방과후 돌봄 연결', 'CARE', '지역 돌봄 기관 정보를 안내하고 연결하는 프로그램', 'GOVERNMENT', 6, 12, 0, true, '은평구', '은평구 은평로 195', 37.602709, 126.929111, '2026-05-25', '2026-12-31', '상시', 'VISIT', 50, 44, true, '2026-12-01', 4.2, 5, true, NULL, '돌봄상담, 기관연계', '02-111-7000', 'https://example.com/program/35', 'MANUAL', 'PRG-035', now(), now(), now()),

      (36, 8, '구로 AI 코딩캠프', 'EDUCATION', 'AI와 코딩 기초를 체험하는 디지털 수업', 'PUBLIC', 10, 13, 0, true, '구로구', '구로구 디지털로 300', 37.485215, 126.901594, '2026-07-01', '2026-08-01', '토 10:00-12:00', 'SMALL', 20, 17, true, '2026-06-20', 4.9, 24, true, NULL, 'AI, 코딩, 프로젝트', '02-111-8000', 'https://example.com/program/36', 'MANUAL', 'PRG-036', now(), now(), now()),
      (37, 8, '초등 컴퓨터 활용', 'EDUCATION', '문서 작성과 발표 자료 제작을 배우는 수업', 'PUBLIC', 9, 13, 10000, false, '구로구', '구로구 디지털로 300', 37.485215, 126.901594, '2026-06-01', '2026-07-30', '수 16:00-17:30', 'SMALL', 18, 16, true, '2026-05-28', 4.3, 7, true, NULL, '문서, 발표, 파일관리', '02-111-8000', 'https://example.com/program/37', 'MANUAL', 'PRG-037', now(), now(), now()),
      (38, 8, '로봇 만들기 체험', 'EXPERIENCE', '간단한 로봇을 조립하고 움직여보는 활동', 'PUBLIC', 8, 12, 30000, false, '구로구', '구로구 디지털로 300', 37.485215, 126.901594, '2026-06-10', '2026-08-10', '토 13:00-15:00', 'SMALL', 15, 13, true, '2026-06-05', 4.8, 18, true, NULL, '로봇조립, 센서, 코딩', '02-111-8000', 'https://example.com/program/38', 'MANUAL', 'PRG-038', now(), now(), now()),
      (39, 8, '디지털 안전 교육', 'EDUCATION', '스마트폰과 인터넷을 안전하게 사용하는 방법을 배우는 교육', 'PUBLIC', 8, 13, 0, true, '구로구', '구로구 디지털로 300', 37.485215, 126.901594, '2026-06-15', '2026-07-15', '월 16:00-17:00', 'MEDIUM', 25, 23, true, '2026-06-10', 4.2, 4, true, NULL, '디지털 안전, 개인정보', '02-111-8000', 'https://example.com/program/39', 'MANUAL', 'PRG-039', now(), now(), now()),
      (40, 8, '구로 온라인 코딩', 'EDUCATION', '온라인으로 진행되는 초등 코딩 기초 수업', 'ONLINE', 9, 13, 50000, false, '구로구', '온라인', 37.485215, 126.901594, '2026-06-20', '2026-08-20', '화 19:00-20:00', 'ONLINE', 30, 29, true, '2026-06-15', 4.5, 10, true, NULL, '온라인 코딩, 과제 피드백', '02-111-8000', 'https://example.com/program/40', 'MANUAL', 'PRG-040', now(), now(), now()),

      (41, 9, '중랑 무료 돌봄교실', 'CARE', '지역 아동을 위한 무료 돌봄 프로그램', 'PUBLIC', 6, 12, 0, true, '중랑구', '중랑구 망우로 350', 37.606324, 127.092623, '2026-05-20', '2026-12-20', '월-금 14:00-18:00', 'MEDIUM', 35, 31, true, '2026-05-18', 4.6, 16, true, NULL, '돌봄, 숙제, 놀이', '02-111-9000', 'https://example.com/program/41', 'MANUAL', 'PRG-041', now(), now(), now()),
      (42, 9, '중랑 생태체험', 'EXPERIENCE', '공원에서 자연 관찰과 생태 활동을 진행', 'PUBLIC', 6, 10, 0, true, '중랑구', '중랑구 망우로 350', 37.606324, 127.092623, '2026-06-03', '2026-07-03', '토 10:00-12:00', 'MEDIUM', 25, 24, true, '2026-05-30', 4.5, 8, true, NULL, '자연관찰, 생태놀이', '02-111-9000', 'https://example.com/program/42', 'MANUAL', 'PRG-042', now(), now(), now()),
      (43, 9, '기초 수영 안전', 'SPORTS', '물놀이 안전과 기초 수영 동작을 배우는 수업', 'PUBLIC', 7, 12, 40000, false, '중랑구', '중랑구 망우로 350', 37.606324, 127.092623, '2026-06-12', '2026-08-12', '수 17:00-18:00', 'MEDIUM', 20, 18, true, '2026-06-08', 4.1, 5, true, NULL, '수영안전, 기초동작', '02-111-9000', 'https://example.com/program/43', 'MANUAL', 'PRG-043', now(), now(), now()),
      (44, 9, '중랑 독서 토론', 'EDUCATION', '책을 읽고 생각을 나누는 독서 토론 프로그램', 'PUBLIC', 9, 13, 10000, false, '중랑구', '중랑구 망우로 350', 37.606324, 127.092623, '2026-06-18', '2026-08-18', '목 17:00-18:30', 'SMALL', 12, 11, true, '2026-06-12', 4.4, 9, true, NULL, '독서, 토론, 발표', '02-111-9000', 'https://example.com/program/44', 'MANUAL', 'PRG-044', now(), now(), now()),
      (45, 9, '중랑 미술놀이', 'ART', '재료 탐색과 만들기를 함께하는 미술놀이', 'PUBLIC', 4, 8, 0, true, '중랑구', '중랑구 망우로 350', 37.606324, 127.092623, '2026-06-20', '2026-07-20', '금 15:00-16:00', 'SMALL', 15, 14, true, '2026-06-15', 4.7, 7, true, NULL, '미술놀이, 만들기', '02-111-9000', 'https://example.com/program/45', 'MANUAL', 'PRG-045', now(), now(), now()),

      (46, 10, '온라인 초등 수학 코칭', 'EDUCATION', '온라인으로 받는 초등 수학 맞춤 코칭', 'ONLINE', 8, 13, 60000, false, '온라인', '온라인', 37.566500, 126.978000, '2026-05-20', '2026-07-20', '월 20:00-21:00', 'ONLINE', 50, 47, true, '2026-05-18', 4.6, 12, true, NULL, '온라인 코칭, 문제풀이', '02-111-1010', 'https://example.com/program/46', 'MANUAL', 'PRG-046', now(), now(), now()),
      (47, 10, '온라인 영어 리딩', 'LANGUAGE', '온라인 영어 원서 읽기와 표현 학습', 'ONLINE', 9, 13, 70000, false, '온라인', '온라인', 37.566500, 126.978000, '2026-06-01', '2026-08-01', '화 20:00-21:00', 'ONLINE', 40, 36, true, '2026-05-29', 4.5, 10, true, NULL, '영어리딩, 표현, 퀴즈', '02-111-1010', 'https://example.com/program/47', 'MANUAL', 'PRG-047', now(), now(), now()),
      (48, 10, '온라인 코딩 프로젝트', 'EDUCATION', '온라인으로 진행되는 프로젝트형 코딩 수업', 'ONLINE', 10, 13, 90000, false, '온라인', '온라인', 37.566500, 126.978000, '2026-06-10', '2026-08-10', '목 20:00-21:30', 'ONLINE', 30, 27, true, '2026-06-05', 4.8, 14, true, NULL, '프로젝트, 코딩, 발표', '02-111-1010', 'https://example.com/program/48', 'MANUAL', 'PRG-048', now(), now(), now()),
      (49, 10, '온라인 부모 상담', 'CARE', '양육 고민을 온라인으로 상담하는 프로그램', 'ONLINE', 3, 13, 0, true, '온라인', '온라인', 37.566500, 126.978000, '2026-05-20', '2026-12-31', '상시', 'ONLINE', 100, 93, true, '2026-12-01', 4.3, 6, true, NULL, '부모상담, 양육코칭', '02-111-1010', 'https://example.com/program/49', 'MANUAL', 'PRG-049', now(), now(), now()),
      (50, 10, '온라인 창의 글쓰기', 'EDUCATION', '생각을 정리하고 짧은 글을 쓰는 온라인 수업', 'ONLINE', 8, 12, 50000, false, '온라인', '온라인', 37.566500, 126.978000, '2026-06-15', '2026-08-15', '수 19:00-20:00', 'ONLINE', 25, 23, true, '2026-06-10', 4.4, 8, true, NULL, '글쓰기, 표현, 피드백', '02-111-1010', 'https://example.com/program/50', 'MANUAL', 'PRG-050', now(), now(), now())
    ON CONFLICT (id) DO NOTHING;

-- =========================================================
-- 5. Program Tags
-- =========================================================
INSERT INTO program_tag (id, program_id, tag) VALUES
                                                  (1, 1, '수학'), (2, 1, '기초학습'), (3, 1, '소규모'),
                                                  (4, 2, '독서'), (5, 2, '글쓰기'), (6, 2, '무료'),
                                                  (7, 3, '과학'), (8, 3, '실험'), (9, 3, '체험'),
                                                  (10, 4, '영어'), (11, 4, '파닉스'),
                                                  (12, 5, '돌봄'), (13, 5, '무료'), (14, 5, '방학'),

                                                  (15, 6, '뮤지컬'), (16, 6, '예술'),
                                                  (17, 7, '미술'), (18, 7, '창의력'),
                                                  (19, 8, '축구'), (20, 8, '체육'),
                                                  (21, 9, '유아'), (22, 9, '감각놀이'),
                                                  (23, 10, '한글'), (24, 10, '예비초등'),

                                                  (25, 11, '코딩'), (26, 11, '디지털'),
                                                  (27, 12, '논술'), (28, 12, '글쓰기'),
                                                  (29, 13, '수학'), (30, 13, '보드게임'),
                                                  (31, 14, '역사'), (32, 14, '토론'),
                                                  (33, 15, '돌봄'), (34, 15, '방과후'),

                                                  (35, 16, '과학'), (36, 16, '탐구'),
                                                  (37, 17, '영어'), (38, 17, '회화'),
                                                  (39, 18, '태권도'), (40, 18, '체육'),
                                                  (41, 19, '학습습관'), (42, 19, '자기주도'),
                                                  (43, 20, '돌봄'), (44, 20, '무료'),

                                                  (45, 21, '미술'), (46, 21, '수채화'),
                                                  (47, 22, '도예'), (48, 22, '체험'),
                                                  (49, 23, '음악'), (50, 23, '유아'),
                                                  (51, 24, '영어'), (52, 24, '그림책'),
                                                  (53, 25, '미술'), (54, 25, '포트폴리오'),

                                                  (55, 26, '농구'), (56, 26, '체육'),
                                                  (57, 27, '요가'), (58, 27, '건강'),
                                                  (59, 28, '줄넘기'), (60, 28, '체력'),
                                                  (61, 29, '놀이체육'), (62, 29, '유아'),
                                                  (63, 30, '스포츠캠프'), (64, 30, '방학'),

                                                  (65, 31, '돌봄지원'), (66, 31, '정부지원'),
                                                  (67, 32, '부모교육'), (68, 32, '무료'),
                                                  (69, 33, '놀이상담'), (70, 33, '부모코칭'),
                                                  (71, 34, '정서지원'), (72, 34, '또래관계'),
                                                  (73, 35, '돌봄연계'), (74, 35, '상담'),

                                                  (75, 36, 'AI'), (76, 36, '코딩'),
                                                  (77, 37, '컴퓨터'), (78, 37, '발표'),
                                                  (79, 38, '로봇'), (80, 38, '코딩'),
                                                  (81, 39, '디지털안전'), (82, 39, '인터넷'),
                                                  (83, 40, '온라인'), (84, 40, '코딩'),

                                                  (85, 41, '돌봄'), (86, 41, '무료'),
                                                  (87, 42, '생태'), (88, 42, '체험'),
                                                  (89, 43, '수영'), (90, 43, '안전'),
                                                  (91, 44, '독서'), (92, 44, '토론'),
                                                  (93, 45, '미술'), (94, 45, '무료'),

                                                  (95, 46, '온라인'), (96, 46, '수학'),
                                                  (97, 47, '온라인'), (98, 47, '영어'),
                                                  (99, 48, '온라인'), (100, 48, '코딩'),
                                                  (101, 49, '온라인'), (102, 49, '부모상담'),
                                                  (103, 50, '온라인'), (104, 50, '글쓰기')
    ON CONFLICT (program_id, tag) DO NOTHING;

-- =========================================================
-- 6. Recommendation Preference / AI Recommendation
-- =========================================================
INSERT INTO recommendation_preference (
    id, user_id, child_id, region, monthly_budget, transport_type, move_time,
    online_preference, class_type, created_at
) VALUES
      (1, 1, 1, '강남구', 'FREE', 'CAR', '10-20MIN', 'ANY', 'SMALL', now()),
      (2, 1, 2, '송파구', '0-10', 'CAR', '20MIN+', 'OFFLINE_ONLY', 'SMALL', now()),
      (3, 2, 3, '마포구', 'ANY', 'CAR', '20MIN+', 'ONLINE_OK', 'SMALL', now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO ai_recommendation (
    id, user_id, child_id, preference_id, program_id,
    total_score, score_distance, score_budget, score_age, score_keyword,
    score_class_type, score_recruiting, score_review,
    rank_no, recommend_reason, is_top3, created_at
) VALUES
      (1, 1, 1, 1, 1, 94.50, 22.00, 20.00, 15.00, 14.00, 9.00, 10.00, 4.50, 1, '민준이의 수학 기초학습 고민과 무료 조건에 가장 잘 맞는 프로그램입니다.', true, now()),
      (2, 1, 1, 1, 3, 91.00, 21.00, 18.00, 14.00, 14.00, 9.00, 10.00, 5.00, 2, '과학 체험을 통해 흥미를 높이고 비용 부담도 낮은 프로그램입니다.', true, now()),
      (3, 1, 1, 1, 11, 88.00, 16.00, 20.00, 13.00, 15.00, 9.00, 10.00, 5.00, 3, '코딩 관심사와 잘 맞고 무료 공공 프로그램이라 추천됩니다.', true, now()),
      (4, 1, 2, 2, 7, 92.00, 22.00, 20.00, 15.00, 14.00, 8.00, 10.00, 3.00, 1, '서아의 미술 관심사와 연령 조건에 잘 맞는 무료 프로그램입니다.', true, now()),
      (5, 2, 3, 3, 48, 90.00, 15.00, 15.00, 15.00, 15.00, 10.00, 10.00, 10.00, 1, '온라인 코딩 프로젝트로 고학년 관심사와 잘 맞습니다.', true, now())
    ON CONFLICT (preference_id, program_id) DO NOTHING;

-- =========================================================
-- 7. Benefit Master / Match / AI Report
-- =========================================================
INSERT INTO benefit_master (
    id, benefit_name, benefit_type, support_amount, support_description,
    apply_link, min_age, max_age, condition_description, region, is_active,
    external_source, external_id, last_synced_at, created_at, updated_at
) VALUES
      (1, '아이돌봄서비스 정부지원', 'VOUCHER', 100000, '돌봄 공백 가정을 위한 아이돌봄 지원', 'https://www.bokjiro.go.kr', 3, 12, '소득 기준 및 맞벌이 여부에 따라 지원 가능', '전국', true, 'MANUAL', 'BEN-001', now(), now(), now()),
      (2, '서울형 아이돌봄비', 'ALLOWANCE', 300000, '가족 돌봄 또는 민간 돌봄 이용 가정 지원', 'https://www.seoul.go.kr', 3, 12, '서울 거주 및 조건 충족 가정 대상', '서울', true, 'MANUAL', 'BEN-002', now(), now(), now()),
      (3, '교육비 바우처', 'VOUCHER', 120000, '교육 활동비 부담 완화를 위한 바우처', 'https://www.gov.kr', 6, 13, '초등 자녀 및 소득 기준 충족 시 신청 가능', '전국', true, 'MANUAL', 'BEN-003', now(), now(), now()),
      (4, '공공 방과후 무료 프로그램', 'FREE_SERVICE', 80000, '지역 공공기관 무료 교육 프로그램 안내', 'https://example.com/benefit/4', 6, 13, '지역 공공기관 모집 기준에 따라 신청 가능', '서울', true, 'MANUAL', 'BEN-004', now(), now(), now()),
      (5, '문화누리 아동 체험 할인', 'DISCOUNT', 50000, '아동 문화 체험 비용 할인 지원', 'https://www.mnuri.kr', 6, 13, '대상 가정 조건 충족 시 이용 가능', '전국', true, 'MANUAL', 'BEN-005', now(), now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO benefit_match (
    id, user_id, child_id, benefit_id, match_status, expected_monthly_saving, matched_at
) VALUES
      (1, 1, 1, 1, 'APPLICABLE', 100000, now()),
      (2, 1, 1, 3, 'APPLICABLE', 120000, now()),
      (3, 1, 2, 2, 'APPLICABLE', 300000, now()),
      (4, 2, 3, 4, 'APPLICABLE', 80000, now()),
      (5, 3, 4, 5, 'NOT_ELIGIBLE', 0, now())
    ON CONFLICT (child_id, benefit_id) DO NOTHING;

INSERT INTO ai_report (
    id, child_id, total_support_count, total_free_program_count,
    total_recommend_count, total_monthly_saving, ai_match_score,
    summary_message, created_at, updated_at
) VALUES
      (1, 1, 2, 12, 3, 220000, 92.50, '민준이는 무료 공공 프로그램과 교육비 바우처를 함께 활용하면 월 양육비 절감 효과가 큽니다.', now(), now()),
      (2, 2, 1, 8, 2, 300000, 88.00, '서아는 미술과 사회성 발달 프로그램 중심으로 추천되며 서울형 아이돌봄비 확인이 필요합니다.', now(), now()),
      (3, 3, 1, 10, 2, 80000, 85.00, '지후는 온라인 코딩과 독서 토론 프로그램을 함께 이용하면 학습 확장에 도움이 됩니다.', now(), now())
    ON CONFLICT (child_id) DO NOTHING;

-- =========================================================
-- 8. Application / Payment / Review
-- =========================================================
INSERT INTO application (
    id, user_id, child_id, program_id,
    applicant_name, parent_name, phone, request_note, ai_start_message,
    agree_terms, agree_privacy, reserve_no, seat_locked_until,
    application_status, applied_at, updated_at
) VALUES
      (1, 1, 1, 1, '민준', '김민지', '010-1111-2222', '수학 기초를 잘 잡고 싶습니다.', '아이의 집중도가 좋은 화요일 시작을 추천합니다.', true, true, 1001, now() + interval '30 minutes', 'PAYMENT_READY', now(), now()),
      (2, 1, 2, 7, '서아', '김민지', '010-1111-2222', '미술 활동을 좋아합니다.', '창의 활동이 많은 프로그램으로 추천됩니다.', true, true, 1002, NULL, 'CONFIRMED', now(), now()),
      (3, 2, 3, 48, '지후', '정아름', '010-3333-4444', '온라인 코딩을 원합니다.', '온라인 수업 적응도가 높을 것으로 보입니다.', true, true, 1003, NULL, 'CONFIRMED', now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO payment (
    id, application_id, payment_method, payment_amount, payment_status,
    order_id, payment_key, failure_code, failure_message,
    approved_at, cancelled_at, created_at, updated_at
) VALUES
      (1, 1, 'FREE', 0, 'READY', 'ORDER-SEED-001', NULL, NULL, NULL, NULL, NULL, now(), now()),
      (2, 2, 'FREE', 0, 'APPROVED', 'ORDER-SEED-002', 'PAYMENT-SEED-002', NULL, NULL, now(), NULL, now(), now()),
      (3, 3, 'TOSS_PAYMENTS', 90000, 'APPROVED', 'ORDER-SEED-003', 'PAYMENT-SEED-003', NULL, NULL, now(), NULL, now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO review (
    id, program_id, user_id, application_id, rating, content, created_at, updated_at
) VALUES
      (1, 7, 1, 2, 5.0, '아이가 미술 활동을 정말 좋아했고 선생님 피드백도 좋았습니다.', now(), now()),
      (2, 48, 2, 3, 4.5, '온라인 수업이지만 프로젝트 중심이라 아이가 흥미를 잃지 않았습니다.', now(), now())
    ON CONFLICT (application_id) DO NOTHING;

-- =========================================================
-- 9. Bookmark / Search / Suggestion / Notification
-- =========================================================
INSERT INTO bookmark (
    id, user_id, program_id, created_at
) VALUES
      (1, 1, 1, now()),
      (2, 1, 3, now()),
      (3, 1, 11, now()),
      (4, 2, 48, now()),
      (5, 3, 45, now())
    ON CONFLICT (user_id, program_id) DO NOTHING;

INSERT INTO search_history (
    id, user_id, keyword, searched_at
) VALUES
      (1, 1, '무료 수학 프로그램', now()),
      (2, 1, '선생님 피드백 좋은 미술', now()),
      (3, 2, '온라인 코딩', now()),
      (4, 3, '유아 미술놀이', now()),
      (5, 3, '방과후 돌봄', now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO ai_search_suggestion (
    id, user_id, suggestion_text, is_global, created_at
) VALUES
      (1, NULL, '무료 공공 돌봄 프로그램', true, now()),
      (2, NULL, '초등 저학년 수학 기초', true, now()),
      (3, NULL, '후기 좋은 미술 수업', true, now()),
      (4, 1, '민준이에게 맞는 코딩 수업', false, now()),
      (5, 1, '서아가 좋아할 창의 미술', false, now()),
      (6, 2, '온라인 코딩 프로젝트', false, now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO notification (
    id, user_id, type, title, message,
    reference_id, reference_type, is_read, created_at
) VALUES
      (1, 1, 'AI_RECOMMENDATION', 'AI 맞춤 추천이 도착했어요', '민준이에게 맞는 TOP3 프로그램을 확인해보세요.', 1, 'PROGRAM', false, now()),
      (2, 1, 'PAYMENT_DONE', '결제가 완료되었어요', '서아의 창의 미술 탐험 신청이 확정되었습니다.', 2, 'PAYMENT', false, now()),
      (3, 2, 'APPLICATION_DONE', '신청이 완료되었어요', '온라인 코딩 프로젝트 신청이 완료되었습니다.', 3, 'APPLICATION', true, now()),
      (4, 3, 'BENEFIT_MATCHED', '받을 수 있는 혜택이 있어요', '문화누리 아동 체험 할인 정보를 확인해보세요.', 5, 'BENEFIT', false, now()),
      (5, 1, 'DEADLINE_SOON', '마감 임박 프로그램이 있어요', '초등 수학 기초반 모집 마감이 가까워졌습니다.', 1, 'PROGRAM', false, now())
    ON CONFLICT (id) DO NOTHING;

-- =========================================================
-- 10. Community
-- =========================================================
INSERT INTO community_post (
    id, user_id, category, child_age, title, content, image_url,
    comment_count, like_count, created_at, updated_at
) VALUES
      (1, 1, 'REVIEW', '8세', '무료 수학 프로그램 괜찮았어요', '아이 수학 기초가 약했는데 공공 프로그램이라 부담 없이 시작하기 좋았습니다.', NULL, 2, 5, now(), now()),
      (2, 1, 'QUESTION', '6세', '송파 미술 수업 추천 받을 수 있을까요?', '창의 미술이나 만들기 수업 찾고 있습니다.', NULL, 1, 3, now(), now()),
      (3, 2, 'INFO', '10세', '온라인 코딩 수업 후기 공유', '프로젝트형 수업이 아이에게 잘 맞는 것 같아요.', NULL, 1, 4, now(), now()),
      (4, 3, 'EDUCATION', '7세', '독서 습관 잡는 방법', '하루 15분부터 시작하니 부담이 적었습니다.', NULL, 0, 2, now(), now()),
      (5, 3, 'CARE', '5세', '방학 돌봄 정보 모으는 중', '무료 돌봄 프로그램 신청 일정 공유해요.', NULL, 0, 1, now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO community_comment (
    id, post_id, user_id, content, like_count, created_at, updated_at
) VALUES
      (1, 1, 2, '저도 이 프로그램 관심 있었는데 후기 감사합니다.', 1, now(), now()),
      (2, 1, 3, '무료인데 구성이 괜찮으면 좋네요.', 0, now(), now()),
      (3, 2, 2, '송파 어린이문화센터 미술 수업 괜찮았어요.', 1, now(), now()),
      (4, 3, 1, '온라인 코딩은 집중 시간이 걱정인데 참고할게요.', 0, now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO post_tag (
    id, post_id, tag
) VALUES
      (1, 1, '수학'),
      (2, 1, '무료'),
      (3, 2, '미술'),
      (4, 2, '추천'),
      (5, 3, '코딩'),
      (6, 3, '온라인'),
      (7, 4, '독서'),
      (8, 5, '돌봄')
    ON CONFLICT (post_id, tag) DO NOTHING;

INSERT INTO post_like (
    id, post_id, user_id, created_at
) VALUES
      (1, 1, 2, now()),
      (2, 1, 3, now()),
      (3, 2, 2, now()),
      (4, 3, 1, now()),
      (5, 4, 1, now())
    ON CONFLICT (post_id, user_id) DO NOTHING;

INSERT INTO comment_like (
    id, comment_id, user_id, created_at
) VALUES
      (1, 1, 1, now()),
      (2, 3, 1, now())
    ON CONFLICT (comment_id, user_id) DO NOTHING;

-- =========================================================
-- 11. Sequence Reset
-- 명시 id insert 이후 다음 자동 증가값 보정
-- =========================================================
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('child_profile_id_seq', (SELECT MAX(id) FROM child_profile));
SELECT setval('child_concern_id_seq', (SELECT MAX(id) FROM child_concern));
SELECT setval('institution_id_seq', (SELECT MAX(id) FROM institution));
SELECT setval('program_id_seq', (SELECT MAX(id) FROM program));
SELECT setval('program_tag_id_seq', (SELECT MAX(id) FROM program_tag));
SELECT setval('recommendation_preference_id_seq', (SELECT MAX(id) FROM recommendation_preference));
SELECT setval('ai_recommendation_id_seq', (SELECT MAX(id) FROM ai_recommendation));
SELECT setval('benefit_master_id_seq', (SELECT MAX(id) FROM benefit_master));
SELECT setval('benefit_match_id_seq', (SELECT MAX(id) FROM benefit_match));
SELECT setval('ai_report_id_seq', (SELECT MAX(id) FROM ai_report));
SELECT setval('application_id_seq', (SELECT MAX(id) FROM application));
SELECT setval('payment_id_seq', (SELECT MAX(id) FROM payment));
SELECT setval('review_id_seq', (SELECT MAX(id) FROM review));
SELECT setval('bookmark_id_seq', (SELECT MAX(id) FROM bookmark));
SELECT setval('search_history_id_seq', (SELECT MAX(id) FROM search_history));
SELECT setval('ai_search_suggestion_id_seq', (SELECT MAX(id) FROM ai_search_suggestion));
SELECT setval('notification_id_seq', (SELECT MAX(id) FROM notification));
SELECT setval('community_post_id_seq', (SELECT MAX(id) FROM community_post));
SELECT setval('community_comment_id_seq', (SELECT MAX(id) FROM community_comment));
SELECT setval('post_tag_id_seq', (SELECT MAX(id) FROM post_tag));
SELECT setval('post_like_id_seq', (SELECT MAX(id) FROM post_like));
SELECT setval('comment_like_id_seq', (SELECT MAX(id) FROM comment_like));