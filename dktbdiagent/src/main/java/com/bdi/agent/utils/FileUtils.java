package com.bdi.agent.utils;

import com.bdi.agent.model.BeliefMap;
import com.bdi.agent.model.PhaseConditions;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.model.json.BeliefConstraintSerializer;
import com.bdi.agent.model.json.BeliefMapSerializer;
import com.bdi.agent.model.json.PhaseConditionsSerializer;
import com.bdi.agent.model.json.ScenarioDeserializer;
import com.bdi.agent.model.util.BeliefConstraint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private static Gson gsonConfig() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Scenario.class, new ScenarioDeserializer())
                .registerTypeAdapter(BeliefMap.class, new BeliefMapSerializer())
                .registerTypeAdapter(PhaseConditions.class, new PhaseConditionsSerializer())
                .registerTypeAdapter(BeliefConstraint.class, new BeliefConstraintSerializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    /**
     * Writes an object to a path, creates new file if there is no file there yet.
     *
     * @param object       object to write to json
     * @param pathToFolder path where object should be created
     * @param newFileName  file that should be created or modified
     */
    public static void writeObjectToJson(Object object, String pathToFolder, String newFileName) {
        Gson gson = gsonConfig();

        try (FileWriter writer = new FileWriter(pathToFolder + newFileName)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets all file names without extension from a specified folder.
     *
     * @param files all files that you want to extract names from
     * @return file names without extensions
     * @throws IOException if something goes wrong during these operations
     */
    public static List<String> removeFileExtensions(List<String> files) {
        return files
                .stream()
                .map(x -> Arrays
                        .stream(x.split("\\."))
                        .findFirst())
                .filter(x -> x.isPresent() && !x.get().isEmpty())
                .map(x -> x.get())
                .toList();
    }

    /**
     * get all file names in folder.
     *
     * @param pathToFolder folder
     * @return list of file names
     * @throws IOException some io exception
     */
    public static List<String> getAllFileNames(String pathToFolder) throws IOException {
        File folder = new File(pathToFolder);
        File[] files = folder.listFiles();
        if (files == null) {
            throw new IOException("No files found in the knowledge folder");
        }
        return Arrays.stream(files)
                .filter(x -> x.isFile())
                .map(x -> x.getName())
                .toList();
    }

    /**
     * Deserialize file into class.
     *
     * @param pathToFolder path to the folder
     * @param newFileName  file name
     * @param clazz        class that will be read into
     * @return class serialized from file
     */
    public static <T> T readJsonFromFile(String pathToFolder, String newFileName, Class<T> clazz) {
        Gson gson = gsonConfig();

        try (FileReader reader = new FileReader("./" + pathToFolder + newFileName)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
