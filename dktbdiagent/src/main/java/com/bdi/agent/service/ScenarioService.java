package com.bdi.agent.service;

import com.bdi.agent.model.Action;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.repository.PhaseConditionRepository;
import com.bdi.agent.repository.ScenarioRepository;
import com.bdi.agent.utils.DefaultScenarioFactory;
import com.bdi.agent.utils.FileUtils;
import com.bdi.agent.utils.ValueConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class ScenarioService {
    @NonNull
    private final ScenarioRepository scenarioRepository;
    @NonNull
    private final PhaseConditionRepository phaseConditionRepository;
    @NonNull
    private final ValueConfiguration valueConfiguration;
    @NonNull
    private final KnowledgeService knowledgeService;

    @Value("${scenarios.folder}")
    public String scenarioFolder;

    /**
     * get all scenario names.
     *
     * @return all scenario names
     */
    public List<String> getAllScenarios() {
        try {
            List<String> files = FileUtils.getAllFileNames(scenarioFolder);
            return FileUtils.removeFileExtensions(files);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * get all scenarios temp.
     *
     * @return return all scenarios
     */
    public List<Scenario> tempGetAllScenarios() {
        return scenarioRepository.findAll();
    }

    public void editScenario(Scenario scenario) {
        this.scenarioRepository.save(scenario);
    }

    public void commitScenario(Scenario scenario) {
        FileUtils.writeObjectToJson(scenario, scenarioFolder, scenario.getName() + ".json");
    }

    /**
     * Gets the scenario by its name.
     *
     * @param scenarioName name of the scenario to get.
     * @return the scenario.
     */
    public Scenario getScenarioByName(String scenarioName) {
        Optional<Scenario> sc = scenarioRepository.findScenarioByName(scenarioName).stream().findFirst();
        if (sc.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "Could not find scenario with name: " + scenarioName);
        }
        return sc.get();
    }

    public void deletePhaseCondition(Long id) {
        this.phaseConditionRepository.deleteById(id);
    }

    /**
     * get by name.
     *
     * @param scenarioName test
     * @return test
     */
    @Transactional
    public Scenario getScenarioByNameFromJson(String scenarioName) {
        Stream.of(new File(".").listFiles())
                .filter(file -> file.isDirectory())
                .forEach(f -> System.out.println(f.getName()));
        try {
            Scenario scenario = FileUtils
                    .readJsonFromFile(
                            scenarioFolder,
                            scenarioName + ".json", Scenario.class);
            scenarioRepository.save(scenario);
            return scenario;
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "Could not find scenario with name: " + scenarioName + " folder: " + scenarioFolder + e);
        }
    }

    /**
     * Gets the knowledge for a scenario by its name.
     *
     * @param scenario  the scenario.
     * @param knowledge the name of the knowledge.
     * @return the knowledge object.
     */
    public Knowledge getKnowledge(Scenario scenario, String knowledge) {
        return scenario.getKnowledgeList().stream()
                .filter(kn -> kn.getIntentionName().equals(knowledge)).findFirst().orElseThrow(() ->
                        new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    /**
     * Checks if a scenario exists.
     *
     * @param scenarioName name of the scenario to check.
     * @return whether the scenario exists or not.
     */
    public boolean scenarioExists(String scenarioName) {
        return !scenarioRepository.findScenarioByName(scenarioName).isEmpty();
    }

    /**
     * Creates a default scenario.
     *
     * @param beliefs       the set of beliefs to include.
     * @param desires       the set of desires to include.
     * @param knowledgeList the list of knowledge objects.
     * @param actions       the list of actions.
     * @param scenarioName  the name of the scenario.
     * @return the created scenario.
     */
    public Scenario  createDefaultScenario(Set<Belief> beliefs, Set<Desire> desires,
                                          List<Knowledge> knowledgeList,
                                          List<Action> actions, String scenarioName) {
        if (scenarioExists(scenarioName)) {
            return scenarioRepository.findScenarioByName(scenarioName).get(0);
        }
        DefaultScenarioFactory scenarioFactory = new DefaultScenarioFactory(valueConfiguration,
                beliefs, desires, knowledgeList, actions);
        Scenario newScenario = scenarioFactory.createDefaultScenario(scenarioName);
        //        for (BeliefMap beliefMap : newScenario.getIntentionMapping().values()) {
        //            BeliefMap test = beliefMap;
        //            this.beliefMapRepository.save(beliefMap);
        //        }
        scenarioRepository.save(newScenario);

        return newScenario;
    }

    /**
     * Deletes a scenario.
     *
     * @param scenario the name of the scenario.
     * @return whether the scenario was successfully deleted.
     */
    public boolean deleteScenario(String scenario) {
        boolean exists = scenarioExists(scenario);
        if (exists) {
            scenarioRepository.deleteAllByName(scenario);
        }
        return exists;
    }

    public void flushRepository() {
        scenarioRepository.flush();
    }
}
