package com.moment.momentbackend.community.repository;

import com.moment.momentbackend.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
