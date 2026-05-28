package com.moment.momentbackend.publicdata.repository;

import com.moment.momentbackend.publicdata.entity.BokjiroCentral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BokjiroCentralRepository extends JpaRepository<BokjiroCentral, Long> {
    Optional<BokjiroCentral> findByExternalSourceAndExternalId(String externalSource, String externalId);
}