package com.moment.momentbackend.review.service;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.review.dto.ReviewListResponse;
import com.moment.momentbackend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getReviewList(Long programId) {
        programRepository.findById(programId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        return reviewRepository.findByProgramIdOrderByCreatedAtDesc(programId)
                .stream()
                .map(ReviewListResponse::of)
                .toList();
    }
}
