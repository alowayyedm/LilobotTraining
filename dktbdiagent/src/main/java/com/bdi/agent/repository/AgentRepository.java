package com.bdi.agent.repository;

import com.bdi.agent.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Boolean existsByUserId(String userId);

    Agent getByUserId(String userId);
}
