package com.moment.momentbackend.community.controller;

import com.moment.momentbackend.community.dto.PostCreateRequest;
import com.moment.momentbackend.community.dto.PostDetailResponse;
import com.moment.momentbackend.community.dto.PostListResponse;
import com.moment.momentbackend.community.dto.PostUpdateRequest;
import com.moment.momentbackend.community.service.CommunityService;
import com.moment.momentbackend.community.type.PostCategory;
import com.moment.momentbackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ApiResponse<PostDetailResponse> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        return ApiResponse.ok(communityService.createPost(userId, request), "게시글이 작성되었습니다.");
    }

    @GetMapping
    public ApiResponse<Page<PostListResponse>> getPostList(
            @RequestParam(required = false) PostCategory category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.ok(communityService.getPostList(category, pageable));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(communityService.getPostDetail(postId));
    }

    @PutMapping("/{postId}")
    public ApiResponse<PostDetailResponse> updatePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        return ApiResponse.ok(communityService.updatePost(userId, postId, request), "게시글이 수정되었습니다.");
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        communityService.deletePost(userId, postId);
        return ApiResponse.ok(null, "게시글이 삭제되었습니다.");
    }
}
