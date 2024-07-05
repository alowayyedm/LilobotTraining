package com.bdi.agent.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.repository.KnowledgeRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:config.properties")
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    @Value("${knowledge.folder}")
    public String knowledgeFolder;

    /**
     * the knowledge array with all intents replies and attributes.
     */
    public static List<Knowledge> knowledgeList;

    /**
     * Create a new knowledge service.
     *
     * @param knowledgeRepository The repository to use
     */
    @Autowired
    public KnowledgeService(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeList = new ArrayList<>();
    }




    /**
     * Initialize the knowledge from a folder.
     */
    public void initializeKnowledge() {
        try {
            List<String> initialized = new ArrayList<>();

            File folder = new File(knowledgeFolder);
            File[] files = folder.listFiles();
            if (files == null) {
                throw new IOException("No files found in the knowledge folder");
            }
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }
                readFromCsv(file.getName(), file.getAbsolutePath());
                fromCsvToKnowledge(file.getName(), file.getAbsolutePath());
                initialized.add(file.getName());
            }

            System.out.println("Initialized " + initialized.size() + " knowledge from files: " + initialized);

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public Knowledge getBySubjectAndAttribute(String knowledge, String subject, String attribute) {
        return knowledgeRepository.findByKnowledgeAndSubjectAndAttribute(knowledge, subject, attribute);
    }

    /**
     * Get random knowledge from all knowledge files.
     *
     * @return The knowledge to return
     */
    public String getKnowledge() {
        List<String> knowledgeFiles = knowledgeRepository.findAllUniqueKnowledgeFiles();
        Random rand = new Random();
        return knowledgeFiles.get(rand.nextInt(knowledgeFiles.size()));
    }

    public List<String> getDefaultScenarios() {
        return knowledgeRepository.findAllUniqueKnowledgeFiles();
    }

    public List<Knowledge> getForScenario(String scenario) {
        return knowledgeRepository.findByKnowledge(scenario);
    }

    /**
     * Get a random response from a knowledge object.
     *
     * @param knowledge The knowledge to choose from
     * @return The random response
     */
    public String getResponse(Knowledge knowledge) {
        List<String> res = knowledge.getValues();
        Random rand = new Random();

        return res.get(rand.nextInt(res.size()));
    }

    private void readFromCsv(String knowledge, String path) throws IOException, CsvException {
        // String knowledgeFile = getKnowledgeFromBlobStorage();
        CSVReader reader = new CSVReader(new FileReader(path));
        List<String[]> records = reader.readAll();

        for (String[] record : records) {
            Knowledge k = new Knowledge();
            k.setType("request");
            k.setSubject(record[0]);
            k.setAttribute(record[1]);
            k.setKnowledge(knowledge);

            List<String> values = new ArrayList<>(List.of(record).subList(2, record.length));
            k.setValues(values);
            knowledgeRepository.save(k);
        }

        reader.close();
    }


    /**
     * This method transforms from CSV to knowledge array.
     *
     * @param knowledge The knowledge file to use
     * @param path the path to read csv from
     * @throws IOException raised when reading a file fails
     * @throws CsvException raised when reading CSV from a file fails
     */
    private void fromCsvToKnowledge(String knowledge, String path) throws IOException, CsvException {
        // String knowledgeFile = getKnowledgeFromBlobStorage();
        CSVReader reader = new CSVReader(new FileReader(path));
        List<String[]> records = reader.readAll();

        for (String[] record : records) {
            Knowledge k = new Knowledge();
            k.setSubject(record[0]);
            k.setAttribute(record[1]);
            k.setKnowledge(knowledge);

            List<String> values = new ArrayList<>(Arrays.asList(record).subList(2, record.length));
            k.setValues(values);
            knowledgeList.add(k);
        }

        reader.close();
    }

    /**
     * This method converts a Knowledge Object Array to the correct format CSV file,
     * the save path is usually the KnowledgeFolder path.
     *
     * @param array The knowledge array we are converting to CSV
     * @param savePath The path where the file will be written
     * @throws IOException This exception will be raised if the writer can not write the file
     */
    public static void fromObjToCsv(Knowledge[] array, String savePath) throws IOException {
        try (FileWriter writer = new FileWriter(savePath)) {
            for (Knowledge knowledge : knowledgeList) {
                writer.append(knowledge.getSubject()).append(",");
                writer.append(knowledge.getAttribute()).append(",");
                appendValues(writer, knowledge.getValues());
                writer.append("\n");
            }
        }
    }

    /**
     * Helper function.
     *
     * @param writer the previous writer
     * @param values the replies from the bot
     * @throws IOException raised if the writer encounters an issue
     */
    private static void appendValues(FileWriter writer, List<String> values) throws IOException {
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            writer.append("\"").append(value).append("\"");
            if (i < values.size() - 1) {
                writer.append(",");
            }
        }
    }

    /**
     * This method saves the given file to a given path. In most use cases that path will point to Knowledge.csv.
     * Any existing files will be replaced by the given file.
     * The method will throw an exception if the given file is not a .csv file.
     *
     * @param file The file that is going to be saved as the Knowledge.csv
     * @param savePath path where the file will be saved, in most cases that path is the $knowledge folder already
     *                 defined
     * @throws IOException the exception will be thrown if the file is not of the type .csv
     */
    private static void saveFileToPath(File file, String savePath) throws IOException {
        // Check if the file is a CSV file
        if (!isCsvFile(file)) {
            throw new IllegalArgumentException("The provided file is not a CSV file.");
        }

        // Create the destination directory if it doesn't exist
        Path destinationDirectory = Path.of(savePath).getParent();
        if (!Files.exists(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
        }

        // Copy the file to the specified path
        Path destinationPath = Path.of(savePath);
        Files.copy(file.toPath(), destinationPath);
    }

    /**
     * This method checks wether the file has a .csv extension.
     *
     * @param file to be checked.
     * @return True if the file is a .csv file, false otherwise.
     */
    private static boolean isCsvFile(File file) {
        // Check if the file name ends with ".csv"
        return file.getName().toLowerCase().endsWith(".csv");
    }

    private String getKnowledgeFromBlobStorage() {
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=dktblobstorage;AccountKey=JRaAWGN9SbJ+gvn5ec0"
                + "brrpuvOPT3HS+VSTyLfJoE4/EQKf9eEVIPGqCeniJCiHUKA4JNYymNDtsl1/TDIjEKA==;"
                + "EndpointSuffix=core.windows.net";

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

    /**
     * Add the knowledge type subject and attribute from a string.
     *
     * @param k The knowledge to set
     * @param s The string to use
     */
    public void addKnowledge(Knowledge k, String s) {
        String[] split = s.split("_");
        if (split.length != 3) {
            throw new IllegalArgumentException("Knowledge is of wrong format");
        }
        k.setType(split[0]);
        k.setSubject(split[1]);
        k.setAttribute(split[2]);
    }
}
