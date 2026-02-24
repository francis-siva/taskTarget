package com.codestudiocorp;

import com.codestudiocorp.controllers.JsonHandler;
import com.codestudiocorp.model.Task;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    static final String JSON_FILE = "./src/main/resources/static/input/data.json";

    public static void main(String[] args) {
        System.out.println("Task scheduler wip [=>]");

        FileAnalyser.readFile(JSON_FILE);

        try {
            JsonNode jsonNode = FileAnalyser.objMapper.readTree(new FileReader(JSON_FILE));

            JsonHandler.setNodeValue("auto maintenance", true, jsonNode, "setTaskCompleted", JSON_FILE);

            Task javaCreationTask = new Task("create own java api", 2, new ArrayList<>(Arrays.asList(1, 3, 4, 5 )), true, false);
            JsonHandler.setNodeValue("schedule", javaCreationTask, jsonNode, "scheduleTask", JSON_FILE);

            FileAnalyser.serializeFile(JSON_FILE, jsonNode);
        }
        catch (IOException ioe) {
            System.err.println("error cause: " + ioe.getMessage());
            throw new RuntimeException(ioe);
        }
        FileAnalyser.writeFile(JSON_FILE);

    }
}