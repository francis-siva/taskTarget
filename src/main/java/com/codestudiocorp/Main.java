package com.codestudiocorp;

import com.codestudiocorp.controllers.*;
import com.codestudiocorp.exceptions.IllegalBusinessNodeException;
import com.codestudiocorp.model.Task;
import com.codestudiocorp.utils.ExceptionMessagePrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static final String JSON_FILE = "./src/main/resources/static/input/data.json";

    public static void main(String[] args) {
        System.out.println("Task scheduler wip [=>]");

        FileAnalyser.readFile(JSON_FILE);

        try {
            System.out.println("::Searching requiredFields in File process::");
            if(FileAnalyser.requiredFields_areInFile(JSON_FILE)) {

                JsonNode jsonNode = FileAnalyser.objMapper.readTree(new FileReader(JSON_FILE));

                JsonHandler.setNodeValue("auto maintenance", true, jsonNode, "setTaskCompleted", JSON_FILE);
                Task javaCreationTask = new Task("create own java api", 2, new ArrayList<>(Arrays.asList(1, 3, 4, 5)), true, false);
                JsonHandler.setNodeValue("schedule", javaCreationTask, jsonNode, "scheduleTask", JSON_FILE);

                JsonHandler.findTaskNode("create own java api", "schedule", jsonNode);
                JsonHandler.setNodeValue("finance tuto", "get more educated on finance", jsonNode, "editTaskLibelle", JSON_FILE);

                ParsableNode priorityParser, activityScopeParser;
                priorityParser = new PriorityParser();
                activityScopeParser = new ActivityScopeParser();

                List<ParsableNode> parsableNodeList = new ArrayList<>(Arrays.asList(priorityParser, activityScopeParser));
                parsableNodeList.forEach(parserNode -> System.out.println(parserNode.getNode(jsonNode).toPrettyString()));

                BusinessNodeParser businessNodeParser = new BusinessNodeParser("activityScope");
                System.out.println(String.format("activityScope values: %s", businessNodeParser.getNode(jsonNode).toPrettyString()));

                FileAnalyser.serializeFile(JSON_FILE, jsonNode);
            }
        }
        catch (IOException ioe) {
            ExceptionMessagePrinter exceptMessagePrinter = new ExceptionMessagePrinter(ioe);
            exceptMessagePrinter.showErrorAndCause();
        }
        catch (IllegalBusinessNodeException ibne) {
            ExceptionMessagePrinter exceptMessagePrinter = new ExceptionMessagePrinter(ibne);
            exceptMessagePrinter.showError(String.format("error: [%s]", ibne.getMessage()));
            exceptMessagePrinter.showCause();
        }
        catch (Exception e) {
            ExceptionMessagePrinter exceptMessagePrinter = new ExceptionMessagePrinter(e);
            exceptMessagePrinter.showErrorAndCause();
        }

        FileAnalyser.writeFile(JSON_FILE);
    }
}