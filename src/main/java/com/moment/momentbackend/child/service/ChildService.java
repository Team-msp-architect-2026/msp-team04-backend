package com.moment.momentbackend.child.service;

import com.moment.momentbackend.child.dto.ChildRequestDto;
import com.moment.momentbackend.child.dto.ChildResponseDto;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildProfileRepository childProfileRepository;

    @Transactional
    public ChildResponseDto createChild(Long userId, ChildRequestDto request) {
        validateAge(request.getBirthDate());

        ChildProfile childProfile = ChildProfile.builder()
                .userId(userId)
                .childName(request.getChildName())
                .birthDate(request.getBirthDate())
                .createdAt(LocalDateTime.now())
                .build();

        childProfile.replaceConcerns(normalizeConcerns(request.getConcerns()));

        ChildProfile savedChildProfile = childProfileRepository.save(childProfile);

        return new ChildResponseDto(savedChildProfile);
    }

    @Transactional(readOnly = true)
    public List<ChildResponseDto> getChildren(Long userId) {
        return childProfileRepository.findAllByUserId(userId).stream()
                .map(ChildResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChildResponseDto getChild(Long userId, Long childId) {
        ChildProfile childProfile = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return new ChildResponseDto(childProfile);
    }

    @Transactional
    public ChildResponseDto updateChild(Long userId, Long childId, ChildRequestDto request) {
        validateAge(request.getBirthDate());

        ChildProfile childProfile = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        childProfile.update(request.getChildName(), request.getBirthDate());
        childProfile.replaceConcerns(normalizeConcerns(request.getConcerns()));

        return new ChildResponseDto(childProfile);
    }

    @Transactional
    public void deleteChild(Long userId, Long childId) {
        ChildProfile childProfile = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        childProfileRepository.delete(childProfile);
    }

    private Set<String> normalizeConcerns(List<String> concerns) {
        Set<String> normalizedConcerns = new LinkedHashSet<>();

        if (concerns == null || concerns.isEmpty()) {
            return normalizedConcerns;
        }

        concerns.stream()
                .filter(concern -> concern != null && !concern.isBlank())
                .map(String::trim)
                .forEach(normalizedConcerns::add);

        return normalizedConcerns;
    }

    private void validateAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 3 || age > 13) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
    }
}