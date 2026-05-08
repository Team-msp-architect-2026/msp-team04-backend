package com.moment.momentbackend.child.repository;

import com.moment.momentbackend.child.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    List<ChildProfile> findAllByUserId(Long userId);
    Optional<ChildProfile> findByIdAndUserId(Long id, Long userId);
}