package com.moment.momentbackend.child.repository;

import com.moment.momentbackend.child.entity.ChildConcern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildConcernRepository extends JpaRepository<ChildConcern, Long> {
    void deleteAllByChildProfileId(Long childProfileId);
}