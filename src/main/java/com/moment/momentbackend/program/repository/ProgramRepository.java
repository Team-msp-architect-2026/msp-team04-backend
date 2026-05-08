package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    @Query("SELECT p FROM Program p " +
            "LEFT JOIN FETCH p.institution " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.id = :id AND p.isPublic = true")
    Optional<Program> findDetailById(@Param("id") Long id);
}