package com.bdi.agent.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Service
public class NluService {

    private static final Logger logger = LoggerFactory.getLogger(NluService.class);

    @Value("${nlu.file}")
    private String nluPath;

    private boolean verifyFileExists() {
        return !Files.exists(Paths.get(nluPath));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getYamlFile() {
        Path filePath = Paths.get(nluPath);
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            Yaml yaml = new Yaml();
            Object yamlData = yaml.load(inputStream);
            if (yamlData instanceof Map) {
                return (List<Map<String, Object>>) ((Map<?, ?>) yamlData).get("nlu");
            } else {
                throw new IllegalArgumentException("Invalid YAML format in file: " + nluPath);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading NLU file: " + nluPath, e);
        }
    }

    /**
     * Get all intents and examples from the YAML file.
     *
     * @return All intents
     */
    public Map<String, List<String>> getIntentsAndExamples() {
        if (verifyFileExists()) {
            throw new IllegalArgumentException("NLU file not found at path: " + nluPath);
        }

        List<Map<String, Object>> nluList = getYamlFile();

        return nluList.stream()
                .collect(Collectors.toMap(
                        intentMap -> (String) intentMap.get("intent"),
                        intentMap -> parseExamples((String) intentMap.get("examples"))
                ));
    }

    /**
     * Update an intent in the YAML file.
     *
     * @param intent The intent to set
     * @param examples The examples to use
     */
    public void updateIntent(String intent, List<String> examples) {
        if (verifyFileExists()) {
            throw new IllegalArgumentException("NLU file not found at path: " + nluPath);
        }

        List<Map<String, Object>> nluList = getYamlFile();

        if (examples.size() == 0) {
            nluList = nluList.stream().filter(m -> !m.get("intent").equals(intent)).toList();
        } else {

            // Update or add the intent and examples
            boolean intentFound = false;
            for (Map<String, Object> intentMap : nluList) {
                if (intentMap.get("intent").equals(intent)) {
                    intentMap.put("examples", String.join("\n", examples.stream().map(e -> "- " + e).toList()) + "\n");
                    intentFound = true;
                    break;
                }
            }

            if (!intentFound) {
                Map<String, Object> newIntentMap = new LinkedHashMap<>();
                newIntentMap.put("intent", intent);
                newIntentMap.put("examples", String.join("\n", examples.stream().map(e -> "- " + e).toList()) + "\n");
                nluList.add(newIntentMap);
            }
        }
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        try {
            Files.write(Paths.get(nluPath), yaml.dump(Collections.singletonMap("nlu", nluList)).getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Error writing to NLU file: " + nluPath, e);
        }
    }

    private List<String> parseExamples(String examples) {
        return Stream.of(examples.split("\n"))
                .map(String::trim)
                .filter(example -> example.startsWith("-"))
                .map(example -> example.substring(1).trim())
                .collect(Collectors.toList());
    }
}