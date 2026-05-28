package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Optional<Institution> findByExternalSourceAndExternalId(
            String externalSource, String externalId);
}