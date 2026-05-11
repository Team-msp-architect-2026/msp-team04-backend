package com.moment.momentbackend.bookmark.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkToggleResponse {

    private Long programId;
    private boolean bookmarked;

    public static BookmarkToggleResponse of(Long programId, boolean bookmarked) {
        return BookmarkToggleResponse.builder()
                .programId(programId)
                .bookmarked(bookmarked)
                .build();
    }
}
