package com.moment.momentbackend.report.repository;

import com.moment.momentbackend.report.entity.AiReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiReportRepository extends JpaRepository<AiReport, Long> {

    Optional<AiReport> findByChildId(Long childId);
}