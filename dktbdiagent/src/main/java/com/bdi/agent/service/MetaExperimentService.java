package com.bdi.agent.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bdi.agent.model.MetaExperiment;
import com.bdi.agent.repository.MetaExperimentRepository;

@Service
public class MetaExperimentService {

    @Autowired
    private MetaExperimentRepository repository;

    public MetaExperiment getByUsername(String username) {
        return repository.findByUsername(username);
    }


    public void updateSessNum(String username, int sessNum) {
        MetaExperiment metaExperiment = repository.findByUsername(username);
        if (metaExperiment != null) {
            metaExperiment.setSessNum(sessNum);
            repository.save(metaExperiment);
        } else {
            throw new RuntimeException("User not found.");
        }
    }

}
