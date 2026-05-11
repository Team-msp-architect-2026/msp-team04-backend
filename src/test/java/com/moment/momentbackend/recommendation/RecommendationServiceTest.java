package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.recommendation.dto.PreferenceRequestDto;
import com.moment.momentbackend.recommendation.dto.PreferenceResponseDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.*;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.recommendation.repository.ProgramQueryRepository;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationPreferenceRepository preferenceRepository;

    @Mock
    private AiRecommendationRepository aiRecommendationRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private ProgramQueryRepository programQueryRepository;

    @Mock
    private ScoringService scoringService;

    // -------- savePreference --------

    @Test
    @DisplayName("선호도 저장 - 정상 케이스")
    void savePreference_success() {
        // given
        Long userId = 1L;
        PreferenceRequestDto request = mockRequest(2L);

        given(childProfileRepository.findByIdAndUserId(2L, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));

        RecommendationPreference saved = RecommendationPreference.builder()
                .userId(userId)
                .childId(2L)
                .monthlyBudget(MonthlyBudget.UNDER_30)
                .transportType(TransportType.PUBLIC)
                .moveTime(MoveTime.UNDER_30)
                .onlinePreference(OnlinePreference.BOTH)
                .classType(ClassType.GROUP)
                .createdAt(LocalDateTime.now())
                .build();
        setField(saved, "id", 10L);

        given(preferenceRepository.save(any())).willReturn(saved);

        // when
        Long result = recommendationService.savePreference(userId, request);

        // then
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("선호도 저장 - 타인 자녀 childId로 요청 시 예외")
    void savePreference_childNotOwned_throwsException() {
        // given
        Long userId = 1L;
        PreferenceRequestDto request = mockRequest(99L);

        given(childProfileRepository.findByIdAndUserId(99L, userId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> recommendationService.savePreference(userId, request))
                .isInstanceOf(CustomException.class);
    }

    // -------- getPreference --------

    @Test
    @DisplayName("선호도 단건 조회 - 정상 케이스")
    void getPreference_success() {
        // given
        Long userId = 1L;
        Long preferenceId = 10L;

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId)
                .childId(2L)
                .monthlyBudget(MonthlyBudget.UNDER_30)
                .transportType(TransportType.PUBLIC)
                .moveTime(MoveTime.UNDER_30)
                .onlinePreference(OnlinePreference.BOTH)
                .classType(ClassType.GROUP)
                .createdAt(LocalDateTime.now())
                .build();
        setField(preference, "id", preferenceId);

        given(preferenceRepository.findByIdAndUserId(preferenceId, userId))
                .willReturn(Optional.of(preference));

        // when
        PreferenceResponseDto result = recommendationService.getPreference(userId, preferenceId);

        // then
        assertThat(result.getId()).isEqualTo(preferenceId);
        assertThat(result.getMonthlyBudget()).isEqualTo(MonthlyBudget.UNDER_30);
    }

    @Test
    @DisplayName("선호도 단건 조회 - 타인 선호도 접근 시 예외")
    void getPreference_notOwned_throwsException() {
        // given
        given(preferenceRepository.findByIdAndUserId(10L, 1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> recommendationService.getPreference(1L, 10L))
                .isInstanceOf(CustomException.class);
    }

    // -------- 헬퍼 --------

    private PreferenceRequestDto mockRequest(Long childId) {
        PreferenceRequestDto dto = new PreferenceRequestDto();
        setField(dto, "childId", childId);
        setField(dto, "monthlyBudget", MonthlyBudget.UNDER_30);
        setField(dto, "transportType", TransportType.PUBLIC);
        setField(dto, "moveTime", MoveTime.UNDER_30);
        setField(dto, "onlinePreference", OnlinePreference.BOTH);
        setField(dto, "classType", ClassType.GROUP);
        return dto;
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}