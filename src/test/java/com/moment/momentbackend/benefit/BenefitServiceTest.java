package com.moment.momentbackend.benefit;

import com.moment.momentbackend.benefit.entity.BenefitMaster;
import com.moment.momentbackend.benefit.entity.BenefitMatch;
import com.moment.momentbackend.benefit.repository.BenefitMasterRepository;
import com.moment.momentbackend.benefit.repository.BenefitMatchRepository;
import com.moment.momentbackend.benefit.service.BenefitService;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)

class BenefitServiceTest {

    @InjectMocks
    private BenefitService benefitService;

    @Mock
    private BenefitMasterRepository benefitMasterRepository;

    @Mock
    private BenefitMatchRepository benefitMatchRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    private BenefitMaster mockBenefit(Long id, Integer minAge, Integer maxAge, boolean isActive) {
        BenefitMaster benefit = mock(BenefitMaster.class);
        when(benefit.getId()).thenReturn(id);
        when(benefit.getBenefitName()).thenReturn("테스트 지원금 " + id);
        when(benefit.getBenefitType()).thenReturn("ALLOWANCE");
        when(benefit.getSupportAmount()).thenReturn(100000);
        when(benefit.getIsActive()).thenReturn(isActive);
        when(benefit.getMinAge()).thenReturn(minAge);
        when(benefit.getMaxAge()).thenReturn(maxAge);
        when(benefit.getRegion()).thenReturn(null);
        return benefit;
    }

    @Test
    @DisplayName("나이 범위 내 자녀 - APPLICABLE 매칭")
    void isEligible_withinAgeRange_applicable() {
        BenefitMaster benefit = mockBenefit(1L, 3, 10, true);
        ChildProfile child = ChildProfile.builder()
                .userId(1L).childName("홍길동")
                .birthDate(LocalDate.now().minusYears(7))
                .createdAt(LocalDateTime.now()).build();

        boolean result = benefitService.isEligible(benefit, 7, child);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("나이 범위 벗어난 자녀 - NOT_ELIGIBLE 매칭")
    void isEligible_outOfAgeRange_notEligible() {
        BenefitMaster benefit = mockBenefit(1L, 3, 6, true);
        ChildProfile child = ChildProfile.builder()
                .userId(1L).childName("홍길동")
                .birthDate(LocalDate.now().minusYears(9))
                .createdAt(LocalDateTime.now()).build();

        boolean result = benefitService.isEligible(benefit, 9, child);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("비활성 지원금 - NOT_ELIGIBLE 매칭")
    void isEligible_inactiveBenefit_notEligible() {
        BenefitMaster benefit = mockBenefit(1L, null, null, false);
        ChildProfile child = ChildProfile.builder()
                .userId(1L).childName("홍길동")
                .birthDate(LocalDate.now().minusYears(5))
                .createdAt(LocalDateTime.now()).build();

        boolean result = benefitService.isEligible(benefit, 5, child);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("나이 제한 없는 지원금 - APPLICABLE 매칭")
    void isEligible_noAgeLimit_applicable() {
        BenefitMaster benefit = mockBenefit(1L, null, null, true);
        ChildProfile child = ChildProfile.builder()
                .userId(1L).childName("홍길동")
                .birthDate(LocalDate.now().minusYears(10))
                .createdAt(LocalDateTime.now()).build();

        boolean result = benefitService.isEligible(benefit, 10, child);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 자녀 매칭 시 예외 발생")
    void recalculate_childNotFound_throwsException() {
        when(childProfileRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> benefitService.recalculate(1L, 99L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("매칭 실행 시 saveAll 호출 확인")
    void recalculate_saveAllCalled() {
        Long userId = 1L;
        Long childId = 1L;

        ChildProfile child = ChildProfile.builder()
                .userId(userId).childName("홍길동")
                .birthDate(LocalDate.now().minusYears(7))
                .createdAt(LocalDateTime.now()).build();

        BenefitMaster benefit = mockBenefit(1L, 3, 10, true);

        when(childProfileRepository.findByIdAndUserId(childId, userId))
                .thenReturn(Optional.of(child));
        when(benefitMasterRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(benefit));
        doNothing().when(benefitMatchRepository).deleteAllByChildId(childId);
        when(benefitMatchRepository.saveAll(any())).thenReturn(List.of());

        benefitService.recalculate(userId, childId);

        verify(benefitMatchRepository, times(1)).deleteAllByChildId(childId);
        verify(benefitMatchRepository, times(1)).saveAll(any());
    }
}