package com.moment.momentbackend.community.repository;
import com.moment.momentbackend.community.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    void deleteByCommentId(Long commentId);
}
