package com.moment.momentbackend.review.repository;

import com.moment.momentbackend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProgramIdOrderByCreatedAtDesc(Long programId);

    long countByProgramId(Long programId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.programId = :programId")
    java.math.BigDecimal findAverageRatingByProgramId(@Param("programId") Long programId);
}
