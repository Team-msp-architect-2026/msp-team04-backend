package com.moment.momentbackend.child.controller;

import com.moment.momentbackend.child.dto.ChildRequestDto;
import com.moment.momentbackend.child.dto.ChildResponseDto;
import com.moment.momentbackend.child.service.ChildService;
import com.moment.momentbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Child", description = "자녀 프로필 API")
@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @Operation(summary = "자녀 프로필 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<ChildResponseDto>> createChild(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChildRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(childService.createChild(userId, request)));
    }

    @Operation(summary = "자녀 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChildResponseDto>>> getChildren(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(childService.getChildren(userId)));
    }

    @Operation(summary = "자녀 상세 조회")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<ChildResponseDto>> getChild(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId) {
        return ResponseEntity.ok(ApiResponse.ok(childService.getChild(userId, childId)));
    }

    @Operation(summary = "자녀 프로필 수정")
    @PutMapping("/{childId}")
    public ResponseEntity<ApiResponse<ChildResponseDto>> updateChild(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId,
            @Valid @RequestBody ChildRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(childService.updateChild(userId, childId, request)));
    }

    @Operation(summary = "자녀 프로필 삭제")
    @DeleteMapping("/{childId}")
    public ResponseEntity<ApiResponse<Void>> deleteChild(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long childId) {
        childService.deleteChild(userId, childId);
        return ResponseEntity.ok(ApiResponse.ok(null, "삭제 완료"));
    }
}