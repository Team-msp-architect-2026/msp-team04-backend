package com.moment.momentbackend.benefit.repository;

import com.moment.momentbackend.benefit.entity.BenefitMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitMasterRepository extends JpaRepository<BenefitMaster, Long> {
    List<BenefitMaster> findAllByIsActiveTrue();
}