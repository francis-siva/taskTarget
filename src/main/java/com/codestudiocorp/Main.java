package com.codestudiocorp;

import com.codestudiocorp.controllers.JsonHandler;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    static final String JSON_FILE = "./src/main/resources/static/input/data.json";

    public static void main(String[] args) {
        System.out.println("Task scheduler wip [=>]");

        FileAnalyser.readFile(JSON_FILE);

        try {
            JsonNode jsonNode = FileAnalyser.objMapper.readTree(new FileReader(JSON_FILE));

            JsonHandler.setNodeValue("auto maintenance", true, jsonNode, "setTaskCompleted", JSON_FILE);

            FileAnalyser.serializeFile(JSON_FILE, jsonNode);
        }
        catch (IOException ioe) {
            System.err.println("error cause: " + ioe.getMessage()  );
            throw new RuntimeException(ioe);
        }
        FileAnalyser.writeFile(JSON_FILE);

    }
}