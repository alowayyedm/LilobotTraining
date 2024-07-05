package com.bdi.agent.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.repository.DesireRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:config.properties")
public class DesireService {

    @Value("${localMode}")
    private boolean localMode;

    private DesireRepository desireRepository;
    private final ActionService actionService;

    @Value("${desires.folder}")
    private String desireFolder;

    // configuration for Azure Blob Storage
    String connectionString = "DefaultEndpointsProtocol=https;AccountName=dktblobstorage;"
                    + "AccountKey=JRaAWGN9SbJ+gvn5ec0brrpuvOPT3HS+VSTyLfJoE4/"
                    + "EQKf9eEVIPGqCeniJCiHUKA4JNYymNDtsl1/TDIjEKA==;EndpointSuffix=core.windows.net";

    /**
     * Create a new desire service.
     *
     * @param desireRepository The repository to use
     * @param actionService THe actions service to use
     */
    public DesireService(DesireRepository desireRepository, ActionService actionService) {
        this.desireRepository = desireRepository;
        this.actionService = actionService;
    }

    public void addDesire(Desire desire) {
        desireRepository.save(desire);
    }

    public Desire getById(Long id) {
        return desireRepository.getById(id);
    }

    public Desire getByDesiresAndName(List<Desire> desires, String name) {
        return desires.stream().filter(x -> x.getName() == name).findFirst().get();
    }

    /**
     * Add desires to the database.
     *
     * @param desires The desires to add
     */
    public void addDesires(Set<Desire> desires) {
        for (Desire desire : desires) {
            desireRepository.save(desire);
        }
    }

    /**
     * Return the current active goal for an agent.
     *
     * @param desires The list of desire to check
     * @return The desire / goal
     */
    public Desire getActiveGoal(List<Desire> desires) {
        for (Desire desire : desires) {
            if (desire.isActive()) {
                return desire;
            }
        }

        return null;
    }

    /**
     * Read the desires from a csv file.
     *
     * @return The read set of desires
     */
    public HashSet<Desire> readDesiresFromCsv(String knowledgePath) {
        HashSet<Desire> result = new HashSet<>();
        String desiresFile = knowledgePath.replace("knowledge", "desire");
        try {
            if (!localMode) {
                desiresFile = getDesiresFromBlobStorage(knowledgePath);
            }

            desiresFile = desireFolder + desiresFile;
            CSVReader reader = new CSVReader(new FileReader(desiresFile));
            List<String[]> records = reader.readAll();

            for (String[] record : records) {
                Desire d = desireRepository.save(new Desire());
                d.setName(record[0]);
                d.setFullName(record[1]);
                d.setPhase(Phase.valueOf(record[2]));
                d.setActive(Boolean.parseBoolean(record[3]));

                actionService.addActionsToDesire(d);
                result.add(d);
                desireRepository.save(d);
            }

            reader.close();

        } catch (IOException | CsvException e) {
            e.printStackTrace();
            System.err.println("readDesiresFromCsv: could not initialize desires");
        }

        return result;
    }

    private String getDesiresFromBlobStorage(String knowledgePath) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString).buildClient();
        String containerName = "bdi";
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        String fileName = knowledgePath.replace("knowledge", "desire");
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
