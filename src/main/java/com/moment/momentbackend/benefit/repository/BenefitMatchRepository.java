package com.moment.momentbackend.benefit.repository;

import com.moment.momentbackend.benefit.entity.BenefitMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BenefitMatchRepository extends JpaRepository<BenefitMatch, Long> {

    @Query("SELECT bm FROM BenefitMatch bm JOIN FETCH bm.benefit WHERE bm.childId = :childId")
    List<BenefitMatch> findAllByChildIdWithBenefit(@Param("childId") Long childId);

    @Query("SELECT bm FROM BenefitMatch bm JOIN FETCH bm.benefit WHERE bm.userId = :userId AND bm.childId = :childId")
    List<BenefitMatch> findAllByUserIdAndChildIdWithBenefit(
            @Param("userId") Long userId,
            @Param("childId") Long childId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM BenefitMatch bm WHERE bm.childId = :childId")
    int deleteAllByChildIdInBulk(@Param("childId") Long childId);
}
