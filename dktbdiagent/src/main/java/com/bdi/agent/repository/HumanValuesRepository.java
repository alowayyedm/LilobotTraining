package com.bdi.agent.repository;

import com.bdi.agent.model.Desire;
import com.bdi.agent.model.HumanValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface HumanValuesRepository extends JpaRepository<HumanValues, Long> {
    HumanValues findByAgentIdAndName(Long agentId, String name);

    List<HumanValues> findByAgentIdOrderByName(Long agentId);

}
