package com.moment.momentbackend.community.service;

import com.moment.momentbackend.community.dto.*;
import com.moment.momentbackend.community.entity.CommunityComment;
import com.moment.momentbackend.community.entity.CommunityPost;
import com.moment.momentbackend.community.entity.PostLike;
import com.moment.momentbackend.community.repository.CommunityCommentRepository;
import com.moment.momentbackend.community.repository.CommunityPostRepository;
import com.moment.momentbackend.community.repository.PostLikeRepository;
import com.moment.momentbackend.community.type.PostCategory;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final PostLikeRepository postLikeRepository;

    // ===================== 게시글 =====================

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

    // ===================== 댓글 =====================

    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CommentCreateRequest request) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        CommunityComment comment = CommunityComment.create(postId, userId, request.getContent());
        communityCommentRepository.save(comment);

        post.increaseCommentCount();

        return CommentResponse.of(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentList(Long postId) {
        communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return communityCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponse::of)
                .toList();
    }

    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        communityCommentRepository.delete(comment);
        post.decreaseCommentCount();
    }

    // ===================== 좋아요 =====================

    @Transactional
    public LikeResponse toggleLike(Long userId, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        boolean liked;

        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresentOrElse(
                        like -> {
                            postLikeRepository.delete(like);
                            post.decreaseLikeCount();
                        },
                        () -> {
                            postLikeRepository.save(PostLike.create(postId, userId));
                            post.increaseLikeCount();
                        }
                );

        liked = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        return LikeResponse.of(postId, liked, post.getLikeCount());
    }
}
