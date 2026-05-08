package com.moment.momentbackend.child;

import com.moment.momentbackend.child.dto.ChildRequestDto;
import com.moment.momentbackend.child.dto.ChildResponseDto;
import com.moment.momentbackend.child.entity.ChildConcern;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.child.service.ChildService;
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
class ChildServiceTest {

    @InjectMocks
    private ChildService childService;

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private ChildConcernRepository childConcernRepository;

    @Test
    @DisplayName("자녀 프로필 생성 성공")
    void createChild_success() {
        Long userId = 1L;
        ChildRequestDto request = mock(ChildRequestDto.class);
        when(request.getChildName()).thenReturn("홍길동");
        when(request.getBirthDate()).thenReturn(LocalDate.now().minusYears(5));
        when(request.getConcerns()).thenReturn(List.of("언어발달", "사회성"));

        ChildProfile savedProfile = ChildProfile.builder()
                .userId(userId)
                .childName("홍길동")
                .birthDate(LocalDate.now().minusYears(5))
                .createdAt(LocalDateTime.now())
                .build();

        when(childProfileRepository.save(any())).thenReturn(savedProfile);

        ChildResponseDto result = childService.createChild(userId, request);

        assertThat(result.getChildName()).isEqualTo("홍길동");
        assertThat(result.getAge()).isEqualTo(5);
        verify(childProfileRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("나이 범위 벗어나면 예외 발생 (3~13세)")
    void createChild_invalidAge() {
        Long userId = 1L;
        ChildRequestDto request = mock(ChildRequestDto.class);
        when(request.getBirthDate()).thenReturn(LocalDate.now().minusYears(2)); // 2살

        assertThatThrownBy(() -> childService.createChild(userId, request))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("본인 자녀 목록 조회")
    void getChildren_success() {
        Long userId = 1L;
        ChildProfile profile = ChildProfile.builder()
                .userId(userId)
                .childName("홍길동")
                .birthDate(LocalDate.now().minusYears(7))
                .createdAt(LocalDateTime.now())
                .build();

        when(childProfileRepository.findAllByUserId(userId)).thenReturn(List.of(profile));

        List<ChildResponseDto> result = childService.getChildren(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChildName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("다른 유저의 자녀 조회 시 예외 발생")
    void getChild_notFound() {
        when(childProfileRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> childService.getChild(1L, 99L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("자녀 삭제 성공")
    void deleteChild_success() {
        Long userId = 1L;
        Long childId = 1L;
        ChildProfile profile = ChildProfile.builder()
                .userId(userId)
                .childName("홍길동")
                .birthDate(LocalDate.now().minusYears(5))
                .createdAt(LocalDateTime.now())
                .build();

        when(childProfileRepository.findByIdAndUserId(childId, userId)).thenReturn(Optional.of(profile));

        childService.deleteChild(userId, childId);

        verify(childProfileRepository, times(1)).delete(profile);
    }
}