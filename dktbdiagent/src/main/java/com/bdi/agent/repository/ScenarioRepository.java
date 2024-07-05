package com.bdi.agent.repository;

import com.bdi.agent.model.Scenario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    public List<Scenario> findScenarioByName(String name);

    public void deleteAllByName(String name);
}
