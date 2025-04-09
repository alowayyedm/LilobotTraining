package com.bdi.agent.service;

import com.bdi.agent.model.*;
import com.bdi.agent.repository.ConvictionRepository;
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
public class ConvictionService {


    @Value("${localMode}")
    private boolean localMode;

    private ConvictionRepository convictionRepository;

    @Value("${minValue}")
    private float minValue;
    @Value("${maxValue}")
    private float maxValue;

    @Value("files/Convictions.csv")
    private String ConvictionsFile;



    public ConvictionService(ConvictionRepository convictionRepository) {
        this.convictionRepository = convictionRepository;
    }

    public void addConviction(Set<Conviction> Convictions, int valueSet) {
        for (Conviction conv : Convictions) {
            if (conv.getValueSet() == valueSet) {
                convictionRepository.save(conv);
            }
        }
    }

    public Conviction getById(Long id) {
        return convictionRepository.getById(id);
    }


    public float getConvictionValue(Set<Conviction> Convictions, String name) {
        for (Conviction conv : Convictions) {
            if (conv.getName().equals(name)) {
                return conv.getValue();
            }
        }

        return 0;
    }


    public HashSet<Conviction> readConvictionsFromCsv(Agent agent) {
        HashSet<Conviction> result = new HashSet<>();

        try {

            CSVReader reader = new CSVReader(new FileReader(ConvictionsFile));
            List<String[]> records = reader.readAll();

            for (String[] record : records) {
                Conviction conv = new Conviction();
                conv.setAgent(agent);
                conv.setName(record[0]);
                conv.setFullName(record[1]);
                conv.setValue(Float.valueOf(record[2]));
                conv.setValueSet(Integer.parseInt(record[3].trim()));
                result.add(conv);
            }


        } catch (IOException | CsvException e) {
            System.err.println("readConvictionsFromCsv: could not initialize human values");
        }

        return result;
    }


}
