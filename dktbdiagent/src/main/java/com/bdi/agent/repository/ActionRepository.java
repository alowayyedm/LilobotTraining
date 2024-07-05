package com.bdi.agent.repository;

import com.bdi.agent.model.Action;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {

    List<Action> findByDesireId(Long desireId);
}
