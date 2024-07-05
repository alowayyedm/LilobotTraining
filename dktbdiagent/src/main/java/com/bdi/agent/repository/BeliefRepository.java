package com.bdi.agent.repository;

import com.bdi.agent.model.Belief;
import com.bdi.agent.model.Knowledge;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeliefRepository extends JpaRepository<Belief, Long> {
}
