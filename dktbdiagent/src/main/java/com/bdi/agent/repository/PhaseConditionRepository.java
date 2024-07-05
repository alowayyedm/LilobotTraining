package com.bdi.agent.repository;

import com.bdi.agent.model.PhaseConditions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseConditionRepository extends JpaRepository<PhaseConditions, Long> {
}
