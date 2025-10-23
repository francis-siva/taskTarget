package com.codestudiocorp.controllers;

import com.codestudiocorp.FileAnalyser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonHandler {
    public static final ArrayList<String> JSON_OPERATION = new ArrayList<>(Arrays.asList("setTaskCompleted"));

    /**
     * To search if a particular value exists in a given Json Object fieldname.</br>
     * e.g. Searching for current available task element before operating on its index
     * @param jsonFieldName
     *        {@code string} field location name Json tree
     * @param searchingValue
     *        {@code string} result to identify in file
     * @param jsonNode
     *        {@code Json data source}
     * @return
     */
    public static boolean isSearchingValue_existsInNodeField(String jsonFieldName, String searchingValue, JsonNode jsonNode) {
        boolean res = false;
        boolean checkJsonObjFieldNamePresence = !jsonNode.findValuesAsText(jsonFieldName).isEmpty();
        boolean checkValuePresenceInJson = jsonNode.findValuesAsText(jsonFieldName).contains(searchingValue);

        return (checkJsonObjFieldNamePresence)? (checkValuePresenceInJson) : res;
    }

    /**
     * To do update operations on certain json nodes value like:
     * <ul>
     *     <li>Passing task state "completed" {@code false} to {@code true} value (reverse case included)</li>
     *     <li>Store completed task (by {@code true} value) in achieved array [WIP]</li>
     *     <li>Add/ Edit task name [WIP]</li>
     * </ul>
     * @param nodeValue
     * @param updatedValue
     * @param jsonNode
     * @param nodeOperation
     * @param pathToFile
     * @throws IOException
     */
    public static void setNodeValue(String nodeValue, Object updatedValue, JsonNode jsonNode, String nodeOperation, String pathToFile) throws IOException {

        if(!JSON_OPERATION.contains(nodeOperation)) {
            System.err.println("Error on [nodeOperation] parameter. Incompatible value given: " + nodeOperation);
        }
        else {

            switch (nodeOperation) {
                case "setTaskCompleted":
                    if(updatedValue.getClass().getSimpleName().equals("Boolean")) {
                        setTaskCompleted(jsonNode, nodeValue, (Boolean) updatedValue, pathToFile);
                    }
                    break;
            }
        }
    }

    /**
     * Edit "completed" node from schedule's array task with {@code boolean} value
     * @param jsonNode
     * @param taskName
     *        {@code String} value from schedule's array
     * @param taskCompletionState
     *        to update with new value for {@code taskName}
     * @param pathToFile
     *        location source to serialization
     * @throws IOException
     */
    private static void setTaskCompleted(JsonNode jsonNode, String taskName, Boolean taskCompletionState, String pathToFile) throws IOException {
        JsonNode scheduleNode = jsonNode.get("schedule");
        System.out.println(scheduleNode);
        List<JsonNode> scheduleList= scheduleNode.findValues("task");

        String fieldName = "task", searchingValue = taskName;

        if(JsonHandler.isSearchingValue_existsInNodeField(fieldName, searchingValue, scheduleNode)) {
            int searchingTaskIndex = scheduleNode.findValuesAsText(fieldName).indexOf(searchingValue);
            System.out.println("searchingTaskIndex: " + searchingTaskIndex + "# => " + scheduleNode.get(searchingTaskIndex));
            JsonNode searchingTask = scheduleNode.get(searchingTaskIndex);
            boolean similarBooleanValuePassed = searchingTask.get("completed").asBoolean() == taskCompletionState;

            if(searchingTask.has("completed") && (!similarBooleanValuePassed)) {
                if(searchingTask.isObject()) {

                    ObjectNode foundTask = (ObjectNode) searchingTask;
                    foundTask.put("completed", taskCompletionState);
                    System.out.println(foundTask);
                    //todo:add completed:true (task) to achieved[]
                    System.out.println("totalTask:" + scheduleList.size());
                    //System.out.println("END: " +jsonNode);

                    FileAnalyser.serializeFile(pathToFile, jsonNode);
                }
            }
        }
    }

}