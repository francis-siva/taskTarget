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
                JsonHandler.setNodeValue(JsonHandler.TASK_TYPE[0], javaCreationTask, jsonNode, "scheduleTask", JSON_FILE);

                JsonHandler.findTaskNode("create own java api", JsonHandler.TASK_TYPE[0], jsonNode);
                JsonHandler.setNodeValue("finance tuto", "get more educated on finance", jsonNode, "editTaskLibelle", JSON_FILE);

                ParsableNode priorityParser, activityScopeParser;
                priorityParser = new PriorityParser();
                activityScopeParser = new ActivityScopeParser();

                List<ParsableNode> parsableNodeList = new ArrayList<>(Arrays.asList(priorityParser, activityScopeParser));
                parsableNodeList.forEach(parserNode -> System.out.println(parserNode.getNode(jsonNode).toPrettyString()));

                BusinessNodeParser businessNodeParser = new BusinessNodeParser("activityScope");
                System.out.println(String.format("activityScope values: %s", businessNodeParser.getNode(jsonNode).toPrettyString()));

                Task taskToCreate = new Task("cloudinary", 2, null, true, false);
                //JsonHandler.setNodeValue(JsonHandler.TASK_TYPE[0], taskToCreate, jsonNode, "scheduleTask", JSON_FILE);
                System.out.println(String.format("Result TaskName: %s", JsonHandler.findTaskNode("create own java api", JsonHandler.TASK_TYPE[0], jsonNode).toPrettyString()));
                System.out.println(String.format("Result of JsonNode TaskObject search:: %s", JsonHandler.findTaskObject("cloudinary", JsonHandler.TASK_TYPE[0], jsonNode)));

                Task taskToCreateUpdatedVers = new Task("Learn to use cloudinary", 2, new ArrayList<>(Arrays.asList(1,3)), true, false);
                JsonHandler.setNodeValue("cloudinary", taskToCreateUpdatedVers, jsonNode, "modifyScheduleTaskValues", JSON_FILE);

                taskToCreate.setTaskName("JavaFX");
                taskToCreate.setPriorityOrder(2);
                taskToCreate.setActivityTypes(new ArrayList<>(Arrays.asList(1,3,4)));
                taskToCreate.setRequired(true);
                //JsonHandler.setNodeValue(JsonHandler.TASK_TYPE[0], taskToCreate, jsonNode, "scheduleTask", JSON_FILE);

                taskToCreate.setTaskName("create app with JavaFX");
                taskToCreate.setPriorityOrder(1);
                JsonHandler.setNodeValue("JavaFX", taskToCreate, jsonNode, "modifyScheduleTaskValues", JSON_FILE);

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