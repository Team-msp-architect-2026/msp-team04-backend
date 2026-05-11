package com.moment.momentbackend.bookmark.repository;

import com.moment.momentbackend.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndProgramId(Long userId, Long programId);

    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndProgramId(Long userId, Long programId);
}
