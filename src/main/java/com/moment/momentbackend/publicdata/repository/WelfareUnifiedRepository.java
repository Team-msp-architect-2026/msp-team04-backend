// repository/WelfareUnifiedRepository.java
package com.moment.momentbackend.publicdata.repository;

import com.moment.momentbackend.publicdata.entity.WelfareUnified;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WelfareUnifiedRepository extends JpaRepository<WelfareUnified, Long> {
    boolean existsBySourceAndOriginalId(String source, String originalId);
    List<WelfareUnified> findByTargetGroupContaining(String keyword);
    List<WelfareUnified> findByIsLocal(Boolean isLocal);
}