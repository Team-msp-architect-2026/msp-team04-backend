package com.moment.momentbackend.child.service;

import com.moment.momentbackend.child.dto.ChildRequestDto;
import com.moment.momentbackend.child.dto.ChildResponseDto;
import com.moment.momentbackend.child.entity.ChildConcern;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildProfileRepository childProfileRepository;
    private final ChildConcernRepository childConcernRepository;

    @Transactional
    public ChildResponseDto createChild(Long userId, ChildRequestDto request) {
        validateAge(request.getBirthDate());

        ChildProfile childProfile = ChildProfile.builder()
                .userId(userId)
                .childName(request.getChildName())
                .birthDate(request.getBirthDate())
                .createdAt(LocalDateTime.now())
                .build();
        childProfileRepository.save(childProfile);

        saveConcerns(childProfile, request.getConcerns());

        return new ChildResponseDto(childProfile);
    }

    public List<ChildResponseDto> getChildren(Long userId) {
        return childProfileRepository.findAllByUserId(userId).stream()
                .map(ChildResponseDto::new)
                .collect(Collectors.toList());
    }

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
        childConcernRepository.deleteAllByChildProfileId(childId);
        saveConcerns(childProfile, request.getConcerns());

        return new ChildResponseDto(childProfile);
    }

    @Transactional
    public void deleteChild(Long userId, Long childId) {
        ChildProfile childProfile = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        childProfileRepository.delete(childProfile);
    }

    private void saveConcerns(ChildProfile childProfile, List<String> concerns) {
        if (concerns != null && !concerns.isEmpty()) {
            concerns.forEach(concern -> {
                ChildConcern childConcern = ChildConcern.builder()
                        .childProfile(childProfile)
                        .concern(concern)
                        .build();
                childConcernRepository.save(childConcern);
            });
        }
    }

    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 3 || age > 13) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
    }
}