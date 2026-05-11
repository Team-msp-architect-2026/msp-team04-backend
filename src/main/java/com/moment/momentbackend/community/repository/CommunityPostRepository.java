package com.moment.momentbackend.community.repository;

import com.moment.momentbackend.community.entity.CommunityPost;
import com.moment.momentbackend.community.type.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<CommunityPost> findByCategoryOrderByCreatedAtDesc(PostCategory category, Pageable pageable);
}
