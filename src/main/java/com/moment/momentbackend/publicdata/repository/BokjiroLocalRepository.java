// repository/BokjiroLocalRepository.java
package com.moment.momentbackend.publicdata.repository;

import com.moment.momentbackend.publicdata.entity.BokjiroLocal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BokjiroLocalRepository extends JpaRepository<BokjiroLocal, Long> {
    boolean existsByServiceId(String serviceId);
}