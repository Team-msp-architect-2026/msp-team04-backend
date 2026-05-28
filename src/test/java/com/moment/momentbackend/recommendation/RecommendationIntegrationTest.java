package com.moment.momentbackend.recommendation;

import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.recommendation.dto.PreferenceRequestDto;
import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.*;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import com.moment.momentbackend.recommendation.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationIntegrationTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationPreferenceRepository preferenceRepository;

    @Mock
    private AiRecommendationRepository aiRecommendationRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private ChildConcernRepository childConcernRepository;

    @Mock
    private com.moment.momentbackend.recommendation.repository.ProgramQueryRepository programQueryRepository;

    @Mock
    private com.moment.momentbackend.recommendation.service.ScoringService scoringService;

    @Test
    @DisplayName("선호도 저장 후 preferenceId 반환 확인")
    void savePreference_returnsId() {
        Long userId = 1L;

        ChildProfile child = ChildProfile.builder()
                .userId(userId)
                .childName("홍길동")
                .birthDate(LocalDate.now().minusYears(7))
                .createdAt(LocalDateTime.now())
                .build();

        PreferenceRequestDto request = mock(PreferenceRequestDto.class);
        when(request.getChildId()).thenReturn(1L);
        when(request.getRegion()).thenReturn("서울");
        when(request.getMonthlyBudget()).thenReturn(MonthlyBudget.ZERO_TO_TEN);
        when(request.getTransportType()).thenReturn(TransportType.WALK);
        when(request.getMoveTime()).thenReturn(MoveTime.UNDER_TEN);
        when(request.getOnlinePreference()).thenReturn(OnlinePreference.ANY);
        when(request.getClassType()).thenReturn(ClassType.INDIVIDUAL);

        when(childProfileRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(child));

        RecommendationPreference savedPreference = mock(RecommendationPreference.class);
        when(savedPreference.getId()).thenReturn(1L);
        when(preferenceRepository.save(any())).thenReturn(savedPreference);

        Long preferenceId = recommendationService.savePreference(userId, request);

        verify(preferenceRepository, times(1)).save(any());
        assertThat(preferenceId).isNotNull();
    }

    @Test
    @DisplayName("추천 결과 - 프로그램 없으면 빈 페이지 반환")
    void recommend_emptyWhenNoPrograms() {
        Long userId = 1L;
        Long childId = 1L;
        Long preferenceId = 1L;

        ChildProfile child = ChildProfile.builder()
                .userId(userId)
                .childName("홍길동")
                .birthDate(LocalDate.now().minusYears(7))
                .createdAt(LocalDateTime.now())
                .build();

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId)
                .childId(childId)
                .createdAt(LocalDateTime.now())
                .build();

        when(childProfileRepository.findByIdAndUserId(childId, userId)).thenReturn(Optional.of(child));
        when(preferenceRepository.findByIdAndUserId(preferenceId, userId)).thenReturn(Optional.of(preference));
        when(childConcernRepository.findByChildProfileId(childId)).thenReturn(List.of());
        when(programQueryRepository.findFilteredPrograms(any(), anyInt())).thenReturn(List.of());
        doNothing().when(aiRecommendationRepository).deleteAllByPreferenceId(preferenceId);

        Page<com.moment.momentbackend.recommendation.dto.RecommendationResponseDto> result =
                recommendationService.recommend(userId, childId, preferenceId, PageRequest.of(0, 10), 0.0, 0.0);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("추천 결과 저장 시 rank_no, is_top3 올바르게 저장")
    void recommend_savesRankAndTop3() {
        Long userId = 1L;
        Long childId = 1L;
        Long preferenceId = 1L;

        ChildProfile child = ChildProfile.builder()
                .userId(userId)
                .childName("홍길동")
                .birthDate(LocalDate.now().minusYears(7))
                .createdAt(LocalDateTime.now())
                .build();

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId).childId(childId)
                .createdAt(LocalDateTime.now())
                .build();

        com.moment.momentbackend.program.entity.Program program1 = mock(com.moment.momentbackend.program.entity.Program.class);
        com.moment.momentbackend.program.entity.Program program2 = mock(com.moment.momentbackend.program.entity.Program.class);
        com.moment.momentbackend.program.entity.Program program3 = mock(com.moment.momentbackend.program.entity.Program.class);
        com.moment.momentbackend.program.entity.Program program4 = mock(com.moment.momentbackend.program.entity.Program.class);

        when(program1.getId()).thenReturn(1L);
        when(program2.getId()).thenReturn(2L);
        when(program3.getId()).thenReturn(3L);
        when(program4.getId()).thenReturn(4L);

        com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto score =
                com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto.builder()
                        .totalScore(new java.math.BigDecimal("80.00"))
                        .scoreDistance(new java.math.BigDecimal("20.00"))
                        .scoreBudget(new java.math.BigDecimal("20.00"))
                        .scoreAge(new java.math.BigDecimal("20.00"))
                        .scoreKeyword(new java.math.BigDecimal("0.00"))
                        .scoreClassType(new java.math.BigDecimal("10.00"))
                        .scoreRecruiting(new java.math.BigDecimal("10.00"))
                        .scoreReview(new java.math.BigDecimal("0.00"))
                        .build();

        when(childProfileRepository.findByIdAndUserId(childId, userId)).thenReturn(Optional.of(child));
        when(preferenceRepository.findByIdAndUserId(preferenceId, userId)).thenReturn(Optional.of(preference));
        when(childConcernRepository.findByChildProfileId(childId)).thenReturn(List.of());
        when(programQueryRepository.findFilteredPrograms(any(), anyInt()))
                .thenReturn(List.of(program1, program2, program3, program4));
        when(scoringService.calculate(any(), any(), anyInt(), anyDouble(), anyDouble(), anyList()))
                .thenReturn(score);
        doNothing().when(aiRecommendationRepository).deleteAllByPreferenceId(preferenceId);
        when(aiRecommendationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        recommendationService.recommend(userId, childId, preferenceId, PageRequest.of(0, 10), 0.0, 0.0);

        verify(aiRecommendationRepository, times(4)).save(argThat(rec -> {
            AiRecommendation r = (AiRecommendation) rec;
            if (r.getRankNo() <= 3) {
                assertThat(r.getIsTop3()).isTrue();
            } else {
                assertThat(r.getIsTop3()).isFalse();
            }
            return true;
        }));
    }
}