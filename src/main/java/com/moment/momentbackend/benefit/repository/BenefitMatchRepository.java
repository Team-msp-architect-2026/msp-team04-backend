package com.moment.momentbackend.benefit.repository;

import com.moment.momentbackend.benefit.entity.BenefitMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BenefitMatchRepository extends JpaRepository<BenefitMatch, Long> {

    @Query("SELECT bm FROM BenefitMatch bm JOIN FETCH bm.benefit WHERE bm.childId = :childId")
    List<BenefitMatch> findAllByChildIdWithBenefit(@Param("childId") Long childId);

    void deleteAllByChildId(Long childId);
}