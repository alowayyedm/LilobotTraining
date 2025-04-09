package com.bdi.agent.service;

import com.bdi.agent.model.Agent;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.HumanValues;
import com.bdi.agent.repository.HumanValuesRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@PropertySource("classpath:config.properties")
public class HumanValuesService {

    @Value("${localMode}")
    private boolean localMode;

    private HumanValuesRepository humanvaluesRepository;

    @Value("${minValue}")
    private float minValue;
    @Value("${maxValue}")
    private float maxValue;

    @Value("files/hValues.csv")
    private String hValuesFile;



    public HumanValuesService(HumanValuesRepository humanValueRepository) {
        this.humanvaluesRepository = humanValueRepository;
    }

    public void addHumanValue(Set<HumanValues> humanValues, int valueSet) {
        for (HumanValues hv : humanValues) {
            if (hv.getValueSet() == valueSet) {
                humanvaluesRepository.save(hv);
            }
        }
    }

    public HumanValues getById(Long id) {
        return humanvaluesRepository.getById(id);
    }


    public float gethumanValueValue(Set<HumanValues> humanValues, String name) {
        for (HumanValues hv : humanValues) {
            if (hv.getName().equals(name)) {
                return hv.getValue();
            }
        }

        return 0;
    }


    public HashSet<HumanValues> readHumanValuesFromCsv(Agent agent) {
        HashSet<HumanValues> result = new HashSet<>();

        try {

            CSVReader reader = new CSVReader(new FileReader(hValuesFile));
            List<String[]> records = reader.readAll();

            for (String[] record : records) {
                HumanValues hv = new HumanValues();
                hv.setAgent(agent);
                hv.setName(record[0]);
                hv.setFullName(record[1]);
                hv.setValue(Float.valueOf(record[2]));
                hv.setValueSet(Integer.parseInt(record[3].trim()));
                result.add(hv);
            }


        } catch (IOException | CsvException e) {
            System.err.println("readHumanValuesFromCsv: could not initialize human values");
        }

        return result;
    }

}
