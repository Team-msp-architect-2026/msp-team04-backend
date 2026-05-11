package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.child.entity.ChildConcern;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.recommendation.dto.RecommendProgressResponseDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.*;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RecommendProgressServiceTest {

    @InjectMocks
    private RecommendProgressService recommendProgressService;

    @Mock
    private RecommendationPreferenceRepository preferenceRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private ChildConcernRepository childConcernRepository;

    @Test
    @DisplayName("7단계 모두 완료 시 percentage 100")
    void progress_allCompleted_returns100() {
        Long userId = 1L, preferenceId = 1L, childId = 2L;

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId).childId(childId)
                .region("서울")
                .monthlyBudget(MonthlyBudget.UNDER_30)
                .moveTime(MoveTime.UNDER_30)
                .classType(ClassType.GROUP)
                .onlinePreference(OnlinePreference.BOTH)
                .transportType(TransportType.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();

        given(preferenceRepository.findByIdAndUserId(preferenceId, userId))
                .willReturn(Optional.of(preference));
        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));
        given(childConcernRepository.findByChildProfileId(childId))
                .willReturn(List.of(mock(ChildConcern.class)));

        RecommendProgressResponseDto result = recommendProgressService.getProgress(userId, preferenceId);

        assertThat(result.getPercentage()).isEqualTo(100);
        assertThat(result.getCompletedCount()).isEqualTo(7);
        assertThat(result.getMissingConditions()).isEmpty();
    }

    @Test
    @DisplayName("아무것도 안 채우면 percentage 0")
    void progress_nothingCompleted_returns0() {
        Long userId = 1L, preferenceId = 1L, childId = 2L;

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId).childId(childId)
                .createdAt(LocalDateTime.now())
                .build();

        given(preferenceRepository.findByIdAndUserId(preferenceId, userId))
                .willReturn(Optional.of(preference));
        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));
        given(childConcernRepository.findByChildProfileId(childId))
                .willReturn(List.of());

        RecommendProgressResponseDto result = recommendProgressService.getProgress(userId, preferenceId);

        assertThat(result.getPercentage()).isEqualTo(0);
        assertThat(result.getCompletedCount()).isEqualTo(0);
        assertThat(result.getMissingConditions()).hasSize(7);
    }

    @Test
    @DisplayName("4단계 완료 시 percentage 57")
    void progress_4completed_returns57() {
        Long userId = 1L, preferenceId = 1L, childId = 2L;

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId).childId(childId)
                .region("서울")
                .monthlyBudget(MonthlyBudget.UNDER_30)
                .moveTime(MoveTime.UNDER_30)
                .classType(ClassType.GROUP)
                .createdAt(LocalDateTime.now())
                .build();

        given(preferenceRepository.findByIdAndUserId(preferenceId, userId))
                .willReturn(Optional.of(preference));
        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));
        given(childConcernRepository.findByChildProfileId(childId))
                .willReturn(List.of());

        RecommendProgressResponseDto result = recommendProgressService.getProgress(userId, preferenceId);

        assertThat(result.getCompletedCount()).isEqualTo(4);
        assertThat(result.getMissingConditions()).hasSize(3);
    }

    @Test
    @DisplayName("타인 preference 접근 시 예외")
    void progress_notOwnedPreference_throwsException() {
        given(preferenceRepository.findByIdAndUserId(1L, 99L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> recommendProgressService.getProgress(99L, 1L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("steps 개수는 항상 7개")
    void progress_stepsAlways7() {
        Long userId = 1L, preferenceId = 1L, childId = 2L;

        RecommendationPreference preference = RecommendationPreference.builder()
                .userId(userId).childId(childId)
                .region("서울")
                .createdAt(LocalDateTime.now())
                .build();

        given(preferenceRepository.findByIdAndUserId(preferenceId, userId))
                .willReturn(Optional.of(preference));
        given(childProfileRepository.findByIdAndUserId(childId, userId))
                .willReturn(Optional.of(mock(ChildProfile.class)));
        given(childConcernRepository.findByChildProfileId(childId))
                .willReturn(List.of());

        RecommendProgressResponseDto result = recommendProgressService.getProgress(userId, preferenceId);

        assertThat(result.getSteps()).hasSize(7);
        assertThat(result.getTotalCount()).isEqualTo(7);
    }
}