package com.moment.momentbackend.program.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.program.dto.ProgramDetailResponseDto;
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
import org.springframework.web.bind.annotation.*;
import com.moment.momentbackend.program.dto.MapPinResponseDto;
import com.moment.momentbackend.program.dto.HomeProgramsResponseDto;
import java.util.List;

@Tag(name = "Program", description = "프로그램 API")
@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    // CI/CD demo no-op change: verifies build, image push, GitOps update, and ArgoCD rollout.
    @Operation(summary = "프로그램 목록 조회",
            description = "status(RECRUITING/CLOSED), category, region 필터 및 페이지네이션 지원")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProgramListResponseDto>>> getPrograms(
            @Parameter(description = "모집상태 (RECRUITING / CLOSED)")
            @RequestParam(required = false) String status,
            @Parameter(description = "카테고리 (EDUCATION/CARE/EXPERIENCE/SPORTS/ART/LANGUAGE/ETC)")
            @RequestParam(required = false) String category,
            @Parameter(description = "지역")
            @RequestParam(required = false) String region,
            @Parameter(description = "화면 필터 (ALL/URGENT/FREE/ONLINE/PUBLIC_SUPPORT)")
            @RequestParam(required = false) String filter,
            @PageableDefault(size = 10, sort = "deadlineDate", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                programService.getPrograms(status, category, region, filter, pageable)));
    }

    @Operation(summary = "홈 화면 프로그램 조회",
            description = "홈 화면용 무료·공공, 마감 임박, 온라인 프로그램 조회")
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeProgramsResponseDto>> getHomePrograms(
            @Parameter(description = "지역")
            @RequestParam(required = false) String region,
            @Parameter(description = "카테고리")
            @RequestParam(required = false) String category) {

        return ResponseEntity.ok(ApiResponse.ok(
                programService.getHomePrograms(region, category)
        ));
    }

    @Operation(summary = "프로그램 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgramDetailResponseDto>> getProgram(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(programService.getProgram(id)));
    }

    @Operation(summary = "지도 핀 조회",
            description = "핀 색상: 공공(BLUE) / 모집중(GREEN) / 민간(ORANGE) / 마감(GRAY)")
    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<MapPinResponseDto>>> getMapPins(
            @Parameter(description = "지역 필터")
            @RequestParam(required = false) String region) {
        return ResponseEntity.ok(ApiResponse.ok(programService.getMapPins(region)));
    }

    @Operation(summary = "내 주변 찾기",
            description = "현재 위치(lat, lng) 기준 반경(radiusKm) 내 프로그램 핀 조회, 거리순 정렬")
    @GetMapping("/map/nearby")
    public ResponseEntity<ApiResponse<List<MapPinResponseDto>>> getNearbyMapPins(
            @Parameter(description = "위도") @RequestParam double lat,
            @Parameter(description = "경도") @RequestParam double lng,
            @Parameter(description = "반경(km), 기본 3km") @RequestParam(defaultValue = "3") double radiusKm,
            @Parameter(description = "최대 개수") @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(ApiResponse.ok(
                programService.getNearbyMapPins(lat, lng, radiusKm, limit)));
    }
}