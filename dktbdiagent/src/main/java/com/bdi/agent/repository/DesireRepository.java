package com.bdi.agent.repository;

import com.bdi.agent.model.Desire;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesireRepository extends JpaRepository<Desire, Long> {

}
