package com.moment.momentbackend.review.repository;

import com.moment.momentbackend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProgramIdOrderByCreatedAtDesc(Long programId);
}
