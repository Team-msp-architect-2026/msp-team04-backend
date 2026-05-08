package com.moment.momentbackend.program.service;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.dto.ProgramDetailResponseDto;
import com.moment.momentbackend.program.dto.ProgramListResponseDto;
import com.moment.momentbackend.program.repository.ProgramListQueryRepository;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramListQueryRepository programListQueryRepository;
    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public Page<ProgramListResponseDto> getPrograms(String status, String category,
                                                    String region, Pageable pageable) {
        return programListQueryRepository
                .findPrograms(status, category, region, pageable)
                .map(ProgramListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public ProgramDetailResponseDto getProgram(Long id) {
        return new ProgramDetailResponseDto(
                programRepository.findDetailById(id)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND))
        );
    }
}