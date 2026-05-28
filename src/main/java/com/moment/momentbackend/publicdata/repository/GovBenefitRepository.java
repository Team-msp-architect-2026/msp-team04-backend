package com.moment.momentbackend.publicdata.repository;

import com.moment.momentbackend.publicdata.entity.GovBenefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GovBenefitRepository extends JpaRepository<GovBenefit, Long> {

    Optional<GovBenefit> findByExternalSourceAndExternalId(
            String externalSource,
            String externalId
    );
}