package com.moment.momentbackend.program.service;

import com.moment.momentbackend.program.dto.ProgramListResponseDto;
import com.moment.momentbackend.program.repository.ProgramListQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramListQueryRepository programListQueryRepository;

    @Transactional(readOnly = true)
    public Page<ProgramListResponseDto> getPrograms(String status, String category,
                                                    String region, Pageable pageable) {
        return programListQueryRepository
                .findPrograms(status, category, region, pageable)
                .map(ProgramListResponseDto::new);
    }
}