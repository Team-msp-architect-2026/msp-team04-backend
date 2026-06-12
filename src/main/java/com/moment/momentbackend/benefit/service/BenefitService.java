package com.moment.momentbackend.benefit.service;

import com.moment.momentbackend.benefit.dto.BenefitMasterResponseDto;
import com.moment.momentbackend.benefit.dto.BenefitMatchResponseDto;
import com.moment.momentbackend.benefit.dto.BenefitSummaryResponseDto;
import com.moment.momentbackend.benefit.entity.BenefitAssessmentProfile;
import com.moment.momentbackend.benefit.entity.BenefitMaster;
import com.moment.momentbackend.benefit.entity.BenefitMatch;
import com.moment.momentbackend.benefit.repository.BenefitAssessmentProfileRepository;
import com.moment.momentbackend.benefit.repository.BenefitMasterRepository;
import com.moment.momentbackend.benefit.repository.BenefitMatchRepository;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private static final String APPLICABLE = "APPLICABLE";
    private static final String CONDITION_CHECK = "CONDITION_CHECK";
    private static final String NOT_ELIGIBLE = "NOT_ELIGIBLE";

    private static final int MAX_MATCH_SAVE_COUNT = 50;

    private static final List<String> CORE_PARENTING_TITLE_KEYWORDS = List.of(
            "아동수당",
            "부모급여",
            "양육수당",
            "자녀장려금",
            "아이돌봄",
            "영유아보육료",
            "보육료",
            "유아학비",
            "누리과정",
            "어린이집 부모부담",
            "차액보육료",
            "부모부담보육료",
            "다자녀",
            "한부모",
            "장애아동수당",
            "장애아동 보육료",
            "첫만남이용권",
            "출산장려금",
            "출산지원금",
            "임신출산"
    );

    private static final List<String> NOISE_TITLE_KEYWORDS = List.of(
            "장학금",
            "청년",
            "청소년",
            "상담",
            "문화강좌",
            "공연",
            "대관",
            "체육시설",
            "학교 밖",
            "꿈드림",
            "동아리",
            "진로",
            "취업",
            "직업",
            "자립준비",
            "북한이탈",
            "재외동포",
            "수능",
            "대학교",
            "고등학교",
            "중고등학교",
            "무상급식",
            "의료비",
            "실명",
            "안전지킴이",
            "학대",
            "범죄",
            "피해",
            "법률",
            "어업",
            "원양",
            "수산",
            "해양",
            "선박",
            "낚시",
            "항로",
            "백신",
            "수출",
            "관세",
            "근로자복지",
            "진폐"
    );

    private static final List<String> NON_PARENT_TARGET_TEXT_KEYWORDS = List.of(
            "시설비",
            "운영비",
            "개보수",
            "보육교사",
            "보육교직원",
            "기관 대상",
            "사업자",
            "단체",
            "종사자",
            "어린이집 운영",
            "센터 운영"
    );

    private static final List<String> INCOME_KEYWORDS = List.of(
            "소득",
            "중위소득",
            "저소득",
            "기초생활",
            "수급자",
            "차상위",
            "법정저소득"
    );

    private static final List<String> NATIONAL_PROVIDERS = List.of(
            "보건복지부",
            "교육부",
            "국세청",
            "성평등가족부",
            "농림축산식품부",
            "고용노동부"
    );

    private final BenefitMasterRepository benefitMasterRepository;
    private final BenefitMatchRepository benefitMatchRepository;
    private final ChildProfileRepository childProfileRepository;
    private final BenefitAssessmentProfileRepository benefitAssessmentProfileRepository;

    @Transactional(readOnly = true)
    public List<BenefitMasterResponseDto> getBenefits() {
        return findParentingBenefitCandidates().stream()
                .limit(MAX_MATCH_SAVE_COUNT)
                .map(BenefitMasterResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BenefitMatchResponseDto> recalculate(Long userId, Long childId) {
        ChildProfile child = getOwnedChild(userId, childId);
        int childAge = calculateAge(child);
        Optional<BenefitAssessmentProfile> profile = benefitAssessmentProfileRepository.findByUserId(userId);

        benefitMatchRepository.deleteAllByChildId(childId);
        benefitMatchRepository.flush();

        List<BenefitMatch> matches = findParentingBenefitCandidates().stream()
                .map(benefit -> toMatch(userId, childId, benefit, childAge, profile))
                .filter(match -> !NOT_ELIGIBLE.equals(match.getMatchStatus()))
                .sorted(matchComparator())
                .limit(MAX_MATCH_SAVE_COUNT)
                .collect(Collectors.toList());

        benefitMatchRepository.saveAll(matches);

        return matches.stream()
                .map(BenefitMatchResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitMatchResponseDto> getMatches(Long userId, Long childId) {
        getOwnedChild(userId, childId);

        return benefitMatchRepository.findAllByUserIdAndChildIdWithBenefit(userId, childId).stream()
                .sorted(matchComparator())
                .limit(MAX_MATCH_SAVE_COUNT)
                .map(BenefitMatchResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BenefitSummaryResponseDto getSummary(Long userId, Long childId) {
        ChildProfile child = getOwnedChild(userId, childId);
        boolean profileCompleted = benefitAssessmentProfileRepository.existsByUserId(userId);

        List<BenefitMatch> matches = benefitMatchRepository.findAllByUserIdAndChildIdWithBenefit(userId, childId).stream()
                .sorted(matchComparator())
                .limit(MAX_MATCH_SAVE_COUNT)
                .collect(Collectors.toList());

        int applicableCount = (int) matches.stream()
                .filter(match -> APPLICABLE.equals(match.getMatchStatus()))
                .count();

        int conditionCheckCount = (int) matches.stream()
                .filter(match -> CONDITION_CHECK.equals(match.getMatchStatus()))
                .count();

        int estimatedMonthlySaving = matches.stream()
                .filter(match -> APPLICABLE.equals(match.getMatchStatus()))
                .map(BenefitMatch::getExpectedMonthlySaving)
                .filter(amount -> amount != null && amount > 0)
                .mapToInt(Integer::intValue)
                .sum();

        List<BenefitMatchResponseDto> benefits = matches.stream()
                .map(BenefitMatchResponseDto::new)
                .collect(Collectors.toList());

        String summaryMessage = buildSummaryMessage(child.getChildName(), applicableCount, conditionCheckCount, profileCompleted);
        String officialCheckMessage = "지원금 자격은 입력 정보와 공공데이터 문구를 기준으로 선별한 결과이며, 최종 신청 가능 여부와 정확한 금액은 각 공식 신청 페이지에서 확인해야 합니다.";

        return new BenefitSummaryResponseDto(
                childId,
                child.getChildName(),
                profileCompleted,
                matches.size(),
                applicableCount,
                conditionCheckCount,
                estimatedMonthlySaving,
                summaryMessage,
                officialCheckMessage,
                benefits
        );
    }

    private ChildProfile getOwnedChild(Long userId, Long childId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));
    }

    private int calculateAge(ChildProfile child) {
        return Period.between(child.getBirthDate(), LocalDate.now()).getYears();
    }

    public boolean isEligible(BenefitMaster benefit, int childAge, ChildProfile child) {
        return benefit != null
                && Boolean.TRUE.equals(benefit.getIsActive())
                && matchesAge(benefit, childAge);
    }

    private BenefitMatch toMatch(
            Long userId,
            Long childId,
            BenefitMaster benefit,
            int childAge,
            Optional<BenefitAssessmentProfile> profile
    ) {
        String status = evaluateStatus(benefit, childAge, profile);
        Integer saving = NOT_ELIGIBLE.equals(status) ? null : estimateMonthlySaving(benefit, childAge);

        return BenefitMatch.builder()
                .userId(userId)
                .childId(childId)
                .benefit(benefit)
                .matchStatus(status)
                .expectedMonthlySaving(saving)
                .matchedAt(LocalDateTime.now())
                .build();
    }

    private List<BenefitMaster> findParentingBenefitCandidates() {
        return benefitMasterRepository.findAllByIsActiveTrue().stream()
                .filter(this::isParentingBenefitCandidate)
                .sorted(benefitComparator())
                .collect(Collectors.toList());
    }

    private boolean isParentingBenefitCandidate(BenefitMaster benefit) {
        if (benefit == null || !Boolean.TRUE.equals(benefit.getIsActive())) {
            return false;
        }

        String title = text(benefit.getBenefitName());
        String fullText = combinedText(benefit);

        if (!containsAny(title, CORE_PARENTING_TITLE_KEYWORDS)) {
            return false;
        }

        if (containsAny(title, NOISE_TITLE_KEYWORDS)) {
            return false;
        }

        if (containsAny(fullText, NON_PARENT_TARGET_TEXT_KEYWORDS)
                && !title.contains("부모부담")
                && !title.contains("보육료")
                && !title.contains("유아학비")) {
            return false;
        }

        return true;
    }

    private String evaluateStatus(
            BenefitMaster benefit,
            int childAge,
            Optional<BenefitAssessmentProfile> profile
    ) {
        if (!Boolean.TRUE.equals(benefit.getIsActive())) {
            return NOT_ELIGIBLE;
        }

        if (!matchesAge(benefit, childAge)) {
            return NOT_ELIGIBLE;
        }

        if (profile.isEmpty()) {
            return CONDITION_CHECK;
        }

        BenefitAssessmentProfile assessment = profile.get();

        if (!matchesRegion(benefit, assessment)) {
            return NOT_ELIGIBLE;
        }

        String title = text(benefit.getBenefitName());
        String fullText = combinedText(benefit);

        if (title.contains("다자녀") && !Boolean.TRUE.equals(assessment.getMultiChildFamily())) {
            return NOT_ELIGIBLE;
        }

        if (title.contains("한부모") && !Boolean.TRUE.equals(assessment.getSingleParent())) {
            return NOT_ELIGIBLE;
        }

        if ((title.contains("장애아동") || title.contains("장애아")) && !Boolean.TRUE.equals(assessment.getDisabledFamilyMember())) {
            return NOT_ELIGIBLE;
        }

        if (containsAny(fullText, INCOME_KEYWORDS)) {
            return CONDITION_CHECK;
        }

        if (fullText.contains("맞벌이") && !Boolean.TRUE.equals(assessment.getDualIncome())) {
            return CONDITION_CHECK;
        }

        if (fullText.contains("양육공백") && !Boolean.TRUE.equals(assessment.getDualIncome())) {
            return CONDITION_CHECK;
        }

        if (fullText.contains("유치원에 다니") || fullText.contains("어린이집") || fullText.contains("재원")) {
            return CONDITION_CHECK;
        }

        if (fullText.contains("다문화") && !Boolean.TRUE.equals(assessment.getMulticulturalFamily())) {
            return CONDITION_CHECK;
        }

        return APPLICABLE;
    }

    private boolean matchesAge(BenefitMaster benefit, int childAge) {
        if (benefit.getMinAge() != null && childAge < benefit.getMinAge()) {
            return false;
        }

        if (benefit.getMaxAge() != null && childAge > benefit.getMaxAge()) {
            return false;
        }

        String title = text(benefit.getBenefitName());

        if (title.contains("아동수당") && !title.contains("장애아동수당")) {
            return childAge < 8;
        }

        if (title.contains("유아교육비") || title.contains("유아학비") || title.contains("누리과정")) {
            return childAge >= 3 && childAge <= 5;
        }

        if (title.contains("첫만남이용권") || title.contains("출산장려금") || title.contains("출산지원금") || title.contains("부모급여")) {
            return childAge < 2;
        }

        String normalized = combinedText(benefit)
                .replaceAll("\\s+", "")
                .replace("만", "");

        Matcher rangeMatcher = Pattern.compile("(\\d{1,2})[~\\-∼](\\d{1,2})세").matcher(normalized);
        if (rangeMatcher.find()) {
            int min = Integer.parseInt(rangeMatcher.group(1));
            int max = Integer.parseInt(rangeMatcher.group(2));
            return childAge >= min && childAge <= max;
        }

        Matcher fromToMatcher = Pattern.compile("(\\d{1,2})세(?:부터|에서)(\\d{1,2})세").matcher(normalized);
        if (fromToMatcher.find()) {
            int min = Integer.parseInt(fromToMatcher.group(1));
            int max = Integer.parseInt(fromToMatcher.group(2));
            return childAge >= min && childAge <= max;
        }

        Matcher underMatcher = Pattern.compile("(\\d{1,2})세미만").matcher(normalized);
        if (underMatcher.find()) {
            int maxExclusive = Integer.parseInt(underMatcher.group(1));
            return childAge < maxExclusive;
        }

        Matcher belowOrEqualMatcher = Pattern.compile("(\\d{1,2})세이하").matcher(normalized);
        if (belowOrEqualMatcher.find()) {
            int maxInclusive = Integer.parseInt(belowOrEqualMatcher.group(1));
            return childAge <= maxInclusive;
        }

        Matcher aboveOrEqualMatcher = Pattern.compile("(\\d{1,2})세이상").matcher(normalized);
        if (aboveOrEqualMatcher.find()) {
            int minInclusive = Integer.parseInt(aboveOrEqualMatcher.group(1));
            return childAge >= minInclusive;
        }

        if (containsAny(normalized, List.of("0~23개월", "0~11개월", "12~23개월", "생후3개월~36개월", "생후3개월이상~36개월"))) {
            return childAge < 2;
        }

        if (containsAny(normalized, List.of("24~36개월"))) {
            return childAge >= 2 && childAge <= 3;
        }

        return true;
    }

    private boolean matchesRegion(BenefitMaster benefit, BenefitAssessmentProfile profile) {
        String benefitRegion = text(benefit.getRegion());
        if (benefitRegion.isBlank()) {
            return true;
        }

        if (isNationalProvider(benefitRegion)) {
            return true;
        }

        String userRegion = text(profile.getRegion());
        String userDistrict = text(profile.getDistrict());
        String userFullRegion = (userRegion + " " + userDistrict).trim();

        if (userRegion.isBlank()) {
            return false;
        }

        if (benefitRegion.equals(userRegion) || benefitRegion.equals(userFullRegion)) {
            return true;
        }

        return !userDistrict.isBlank()
                && benefitRegion.contains(userRegion)
                && benefitRegion.contains(userDistrict);
    }

    private boolean isNationalProvider(String region) {
        String normalized = region
                .replace("(", "")
                .replace(")", "")
                .replace("재", "")
                .trim();

        return NATIONAL_PROVIDERS.stream().anyMatch(provider -> provider.equals(normalized) || region.equals(provider));
    }

    private Integer estimateMonthlySaving(BenefitMaster benefit, int childAge) {
        String title = text(benefit.getBenefitName());

        if (title.contains("아동수당")) {
            return childAge < 8 ? 100000 : null;
        }

        if (title.contains("부모급여")) {
            if (childAge < 1) {
                return 1000000;
            }
            if (childAge < 2) {
                return 500000;
            }
            return null;
        }

        if (benefit.getSupportAmount() != null && benefit.getSupportAmount() > 0) {
            return benefit.getSupportAmount();
        }

        return parseMonthlyAmount(combinedText(benefit));
    }

    private Integer parseMonthlyAmount(String sourceText) {
        List<Pattern> patterns = List.of(
                Pattern.compile("(?:월|매월|매달)[^0-9]{0,30}([0-9,]+)\\s*만원"),
                Pattern.compile("([0-9,]+)\\s*만원\\s*/\\s*월"),
                Pattern.compile("1인당\\s*([0-9,]+)\\s*만원"),
                Pattern.compile("월\\s*([0-9,]+)\\s*만원"),
                Pattern.compile("(?:월|매월|매달)[^0-9]{0,30}([0-9,]+)\\s*천원"),
                Pattern.compile("(?:월|매월|매달)[^0-9]{0,30}([0-9,]+)\\s*원")
        );

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(sourceText);
            if (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1).replace(",", ""));
                String matched = matcher.group(0);

                if (matched.contains("만원")) {
                    return number * 10000;
                }
                if (matched.contains("천원")) {
                    return number * 1000;
                }
                return number;
            }
        }

        return null;
    }

    private Comparator<BenefitMaster> benefitComparator() {
        return Comparator
                .comparingInt(this::benefitPriority)
                .thenComparing(benefit -> text(benefit.getRegion()))
                .thenComparing(BenefitMaster::getId);
    }

    private Comparator<BenefitMatch> matchComparator() {
        return Comparator
                .comparingInt((BenefitMatch match) -> matchStatusPriority(match.getMatchStatus()))
                .thenComparing(match -> benefitPriority(match.getBenefit()))
                .thenComparing(match -> text(match.getBenefit().getRegion()))
                .thenComparing(match -> match.getBenefit().getId());
    }

    private int benefitPriority(BenefitMaster benefit) {
        String title = text(benefit.getBenefitName());

        if (title.contains("아동수당")) return 1;
        if (title.contains("부모급여")) return 2;
        if (title.contains("아이돌봄")) return 3;
        if (title.contains("유아학비")) return 4;
        if (title.contains("보육료")) return 5;
        if (title.contains("누리과정")) return 6;
        if (title.contains("자녀장려금")) return 7;
        if (title.contains("다자녀")) return 8;
        if (title.contains("한부모")) return 9;
        if (title.contains("장애아동")) return 10;
        if (title.contains("출산")) return 20;
        return 50;
    }

    private int matchStatusPriority(String status) {
        if (APPLICABLE.equals(status)) return 1;
        if (CONDITION_CHECK.equals(status)) return 2;
        return 9;
    }

    private String buildSummaryMessage(
            String childName,
            int applicableCount,
            int conditionCheckCount,
            boolean profileCompleted
    ) {
        String name = text(childName).isBlank() ? "아이" : childName;

        if (!profileCompleted) {
            return "지원금 진단 정보를 입력하면 " + name + "에게 맞는 육아 지원 혜택을 더 정확히 찾아드릴 수 있어요.";
        }

        if (applicableCount > 0) {
            return name + "에게 신청 가능성이 높은 육아 지원 혜택을 찾았어요.";
        }

        if (conditionCheckCount > 0) {
            return name + "에게 조건 확인이 필요한 육아 지원 혜택을 찾았어요.";
        }

        return name + "의 조건에 맞는 지원 혜택을 계속 확인하고 있어요.";
    }

    private boolean containsAny(String text, List<String> keywords) {
        if (text == null || text.isBlank()) {
            return false;
        }

        return keywords.stream().anyMatch(text::contains);
    }

    private String combinedText(BenefitMaster benefit) {
        return String.join(" ",
                text(benefit.getBenefitName()),
                text(benefit.getConditionDescription()),
                text(benefit.getSupportDescription())
        );
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
