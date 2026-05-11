package com.moment.momentbackend.community.dto;

import com.moment.momentbackend.community.type.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotNull(message = "카테고리는 필수입니다")
    private PostCategory category;

    private String childAge;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    private String imageUrl;
}
