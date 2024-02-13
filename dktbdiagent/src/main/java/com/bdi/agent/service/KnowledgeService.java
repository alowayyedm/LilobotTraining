package com.bdi.agent.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.repository.KnowledgeRepository;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
@PropertySource("classpath:config.properties")
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    @Value("${knowledge.folder}")
    private String knowledgeFolder;

    @Autowired
    public KnowledgeService(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    public void initializeKnowledge() {
        try {
            List<String> initialized = new ArrayList<>();

            File folder = new File(knowledgeFolder);
            File[] files = folder.listFiles();
            if (files == null)
                throw new IOException("No files found in the knowledge folder");
            for (File file : files) {
                if (!file.isFile())
                    continue;
                readFromCsv(file.getName(), file.getAbsolutePath());
                initialized.add(file.getName());
            }
            
            System.out.println("Initialized " + initialized.size() + " knowledge from files: " + initialized);

        } catch (IOException | CsvException e) {
            System.err.println("could not initialize knowledge");
        }
    }

    public Knowledge getBySubjectAndAttribute(String knowledge, String subject, String attribute) {
        return knowledgeRepository.findByKnowledgeAndSubjectAndAttribute(knowledge, subject, attribute);
    }

    public String getKnowledge(String userId) {
        List<String> knowledgeFiles = knowledgeRepository.findAllUniqueKnowledgeFiles();
        Random rand = new Random();
        return knowledgeFiles.get(rand.nextInt(knowledgeFiles.size()));
    }

    public String getResponse(Knowledge knowledge) {
        List<String> res = knowledge.getValues();
        Random rand = new Random();

        return res.get(rand.nextInt(res.size()));
    }

    private void readFromCsv(String id, String path) throws IOException, CsvException {
        // String knowledgeFile = getKnowledgeFromBlobStorage();
        CSVReader reader = new CSVReader(new FileReader(path));
        List<String[]> records = reader.readAll();

        for (String[] record : records) {
            Knowledge k = new Knowledge();
            k.setSubject(record[0]);
            k.setAttribute(record[1]);
            k.setId(id);

            List<String> values = new ArrayList<>();
            values.add(record[2]);
            values.add(record[3]);
            values.add(record[4]);
            values.add(record[5]);
            k.setValues(values);
            knowledgeRepository.save(k);
        }

        reader.close();
    }

    private String getKnowledgeFromBlobStorage() {
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=dktblobstorage;AccountKey=JRaAWGN9SbJ+gvn5ec0brrpuvOPT3HS+VSTyLfJoE4/EQKf9eEVIPGqCeniJCiHUKA4JNYymNDtsl1/TDIjEKA==;EndpointSuffix=core.windows.net";

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
        String containerName = "bdi";
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        String fileName = "knowledge.csv";
        String downloadFileName = fileName.replace(".csv", "DOWNLOAD.csv");
        File downloadedFile = new File(downloadFileName);
        System.out.println("\nDownloading blob to\n\t " + downloadFileName);

        if (!downloadedFile.exists()) {
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            blobClient.downloadToFile(downloadFileName);
        }

        return downloadedFile.getAbsolutePath();
    }
}
