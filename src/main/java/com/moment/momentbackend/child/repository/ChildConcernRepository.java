package com.moment.momentbackend.child.repository;

import com.moment.momentbackend.child.entity.ChildConcern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildConcernRepository extends JpaRepository<ChildConcern, Long> {
    void deleteAllByChildProfileId(Long childProfileId);
    List<ChildConcern> findByChildProfileId(Long childProfileId);
}