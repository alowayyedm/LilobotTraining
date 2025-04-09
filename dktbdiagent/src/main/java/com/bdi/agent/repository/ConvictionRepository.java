package com.bdi.agent.repository;

import com.bdi.agent.model.Conviction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ConvictionRepository extends JpaRepository<Conviction, Long> {
    Conviction findByAgentIdAndName(Long agentId, String name);

    List<Conviction> findByAgentIdOrderByName(Long agentId);
}
