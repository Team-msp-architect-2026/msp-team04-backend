package com.moment.momentbackend.community.service;

import com.moment.momentbackend.community.dto.*;
import com.moment.momentbackend.community.entity.CommentLike;
import com.moment.momentbackend.community.entity.CommunityComment;
import com.moment.momentbackend.community.entity.CommunityPost;
import com.moment.momentbackend.community.entity.PostLike;
import com.moment.momentbackend.community.repository.CommentLikeRepository;
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
    private final CommentLikeRepository commentLikeRepository;

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
        return PostDetailResponse.of(communityPostRepository.save(post), false);
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
    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        boolean isLikedByMe = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        return PostDetailResponse.of(post, isLikedByMe);
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

        boolean isLikedByMe = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        return PostDetailResponse.of(post, isLikedByMe);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 댓글 좋아요 → 댓글 → 게시글 좋아요 → 게시글 순서로 삭제
        List<CommunityComment> comments = communityCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        for (CommunityComment comment : comments) {
            commentLikeRepository.deleteByCommentId(comment.getId());
        }
        communityCommentRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
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

        return CommentResponse.of(comment, false, true);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentList(Long userId, Long postId) {
        communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return communityCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> {
                    boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId);
                    boolean isMine = comment.getUserId().equals(userId);
                    return CommentResponse.of(comment, likedByMe, isMine);
                })
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

        // 댓글 좋아요 먼저 삭제 후 댓글 삭제
        commentLikeRepository.deleteByCommentId(commentId);
        communityCommentRepository.delete(comment);
        post.decreaseCommentCount();
    }

    @Transactional
    public CommentLikeResponse toggleCommentLike(Long userId, Long postId, Long commentId) {
        communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresentOrElse(
                        like -> {
                            commentLikeRepository.delete(like);
                            comment.decreaseLikeCount();
                        },
                        () -> {
                            commentLikeRepository.save(CommentLike.create(commentId, userId));
                            comment.increaseLikeCount();
                        }
                );

        boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        return CommentLikeResponse.of(commentId, likedByMe, comment.getLikeCount());
    }

    // ===================== 좋아요 =====================

    @Transactional
    public LikeResponse toggleLike(Long userId, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

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

        boolean liked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        return LikeResponse.of(postId, liked, post.getLikeCount());
    }
}
