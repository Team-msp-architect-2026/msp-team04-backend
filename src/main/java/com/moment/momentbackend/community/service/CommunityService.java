package com.moment.momentbackend.community.service;

import com.moment.momentbackend.community.dto.PostCreateRequest;
import com.moment.momentbackend.community.dto.PostDetailResponse;
import com.moment.momentbackend.community.dto.PostListResponse;
import com.moment.momentbackend.community.dto.PostUpdateRequest;
import com.moment.momentbackend.community.entity.CommunityPost;
import com.moment.momentbackend.community.repository.CommunityPostRepository;
import com.moment.momentbackend.community.type.PostCategory;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;

    @Transactional
    public PostDetailResponse createPost(Long userId, PostCreateRequest request) {
        CommunityPost post = CommunityPost.create(
                userId,
                request.getCategory(),
                request.getChildAge(),
                request.getTitle(),
                request.getContent(),
                request.getImageUrl()
        );
        return PostDetailResponse.of(communityPostRepository.save(post));
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getPostList(PostCategory category, Pageable pageable) {
        if (category == null) {
            return communityPostRepository.findAllByOrderByCreatedAtDesc(pageable)
                    .map(PostListResponse::of);
        }
        return communityPostRepository.findByCategoryOrderByCreatedAtDesc(category, pageable)
                .map(PostListResponse::of);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return PostDetailResponse.of(post);
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        post.update(
                request.getCategory(),
                request.getChildAge(),
                request.getTitle(),
                request.getContent(),
                request.getImageUrl()
        );
        return PostDetailResponse.of(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        communityPostRepository.delete(post);
    }
}
