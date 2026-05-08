package com.moment.momentbackend.program.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.program.dto.ProgramListResponseDto;
import com.moment.momentbackend.program.service.ProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Program", description = "프로그램 목록 API")
@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @Operation(summary = "프로그램 목록 조회",
            description = "status(RECRUITING/CLOSED), category, region 필터 및 페이지네이션 지원. " +
                    "sort: deadlineDate(마감임박), ratingAvg(평점순), price(가격순)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProgramListResponseDto>>> getPrograms(
            @Parameter(description = "모집상태 (RECRUITING / CLOSED)")
            @RequestParam(required = false) String status,
            @Parameter(description = "카테고리 (EDUCATION/CARE/EXPERIENCE/SPORTS/ART/LANGUAGE/ETC)")
            @RequestParam(required = false) String category,
            @Parameter(description = "지역")
            @RequestParam(required = false) String region,
            @PageableDefault(size = 10, sort = "deadlineDate", direction = Sort.Direction.ASC)
            Pageable pageable) {

        Page<ProgramListResponseDto> result = programService.getPrograms(status, category, region, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}