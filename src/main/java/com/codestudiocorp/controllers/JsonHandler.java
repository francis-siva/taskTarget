package com.codestudiocorp.controllers;

import com.codestudiocorp.FileAnalyser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonHandler {
    public static final ArrayList<String> JSON_OPERATION = new ArrayList<>(Arrays.asList("setTaskCompleted", "setTaskAchieved"));
    public static final String TASK_FIELD = "task";

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
                case "setTaskAchieved":
                    setTaskToAchieved(jsonNode, nodeValue, pathToFile);
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
        JsonNode scheduleNode = getscheduleNode(jsonNode);
        List<JsonNode> scheduleList= scheduleNode.findValues(TASK_FIELD);

        String fieldName = TASK_FIELD, searchingValue = taskName;

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
                    System.out.println("totalTask:" + scheduleList.size());
                    //System.out.println("END: " +jsonNode);

                    FileAnalyser.serializeFile(pathToFile, jsonNode);

                    setTaskToAchieved(jsonNode, taskName, pathToFile);//implicit call to AchievedTask once Completed
                }
            }
        }
        else {
            System.out.println(String.format("%1$s \"%2$s\" is no more/ never set in [schedule]", TASK_FIELD.toUpperCase(), taskName));
        }
    }

    private static void setTaskToAchieved(JsonNode jsonNode, String taskName, String pathToFile) throws IOException {
        JsonNode scheduleNode = getscheduleNode(jsonNode);

        int searchingTaskIndex = getTaskIndex(jsonNode, taskName);

        // Check if working with concrete task index before further process
        if (searchingTaskIndex >= 0) {
            System.out.println("searchingTaskIndex: " + searchingTaskIndex + "# => " + scheduleNode.get(searchingTaskIndex));
            JsonNode searchingTask = scheduleNode.get(searchingTaskIndex);

            JsonNode taskCopy = searchingTask.deepCopy();

            //((ObjectNode) taskCopy).put("task", "repair product done");
            System.out.println("searchingTask: " + searchingTask + "\n taskCopy" + taskCopy);

            JsonNode achievedNode = getachievedNode(jsonNode);
            achievedNode = ((ArrayNode) achievedNode).add(taskCopy);//todo: to uncomment once method defined (addTask ScheduleNode& AchievedNode)
            System.out.println("achievedNode: " + achievedNode);

            if(isTaskAchieved(jsonNode, taskCopy)) {
                deleteScheduleTask(scheduleNode, searchingTask);
            }

            FileAnalyser.serializeFile(pathToFile, jsonNode);
        }
        else {
            System.out.println(String.format("%1$s \"%2$s\" is not in [schedule]", TASK_FIELD.toUpperCase(), taskName));
        }
    }//todo:implement task editor feature


    private static JsonNode getscheduleNode(JsonNode jsonNode) { return jsonNode.get("schedule"); }
    private static JsonNode getachievedNode(JsonNode jsonNode) { return jsonNode.get("achieved"); }
    //jsonNode.findValue("achieved");todo: define in FileAnalyser a mainControl feature of requiring field in file

    /**
     * To retrieve the index of an existing node with {@code taskName} value
     * @param jsonNode represents Json tree
     *        {@code JsonNode}
     * @param taskName Task's name to find among schedule node
     *        {@code JsonNode}
     * @return index of given {@code taskName} if found else -1
     */
    private static int getTaskIndex(JsonNode jsonNode, String taskName) {
        int taskIndex = -1;

        JsonNode scheduleNode = getscheduleNode(jsonNode);
        String fieldName = TASK_FIELD, searchingValue = taskName;

        if(JsonHandler.isSearchingValue_existsInNodeField(fieldName, searchingValue, scheduleNode)) {
            taskIndex = scheduleNode.findValuesAsText(fieldName).indexOf(searchingValue);
        }
        return taskIndex;
    }

    /**
     * To check if a task belongs to achieved array's node
     * @param jsonNode represents Json tree
     *        {@code JsonNode}
     * @param taskNode Task Json value to find among achieved node
     *        {@code JsonNode}
     * @return {@code true} if task is found else {@code false}
     */
    private static boolean isTaskAchieved(JsonNode jsonNode, JsonNode taskNode) {
        return getachievedNode(jsonNode).findValuesAsText(TASK_FIELD).contains(taskNode.get(TASK_FIELD).textValue());
    }

    //Delete Task from schedule[] once placed in achieved[] array
    private static void deleteScheduleTask(JsonNode jsonNode, JsonNode taskNode) {
        int taskIndex = jsonNode.findValues(TASK_FIELD).indexOf(taskNode.get(TASK_FIELD));

        if( taskIndex >= 0) { ((ArrayNode) jsonNode).remove(taskIndex);}
    }
}