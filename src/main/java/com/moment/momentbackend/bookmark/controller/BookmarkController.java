package com.moment.momentbackend.bookmark.controller;

import com.moment.momentbackend.bookmark.dto.BookmarkResponse;
import com.moment.momentbackend.bookmark.dto.BookmarkToggleResponse;
import com.moment.momentbackend.bookmark.service.BookmarkService;
import com.moment.momentbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmark", description = "북마크 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{programId}")
    @Operation(summary = "북마크 추가/해제 토글")
    public ApiResponse<BookmarkToggleResponse> toggle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long programId
    ) {
        return ApiResponse.ok(bookmarkService.toggle(userId, programId));
    }

    @GetMapping
    @Operation(summary = "북마크 목록 조회")
    public ApiResponse<List<BookmarkResponse>> getBookmarkList(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.ok(bookmarkService.getBookmarkList(userId));
    }
}
