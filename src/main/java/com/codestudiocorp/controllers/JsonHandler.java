package com.codestudiocorp.controllers;

import com.codestudiocorp.FileAnalyser;
import com.codestudiocorp.model.Task;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonHandler {
    public static final ArrayList<String> JSON_OPERATION = new ArrayList<>(Arrays.asList("scheduleTask", "setTaskCompleted", "setTaskAchieved", "editTaskLibelle"));
    public static final String TASK_FIELD = "task";
    public static final String[] TASK_TYPE = {"schedule", "achieved"};

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
//        boolean res = false;
        boolean checkJsonObjFieldNamePresence = !jsonNode.findValuesAsText(jsonFieldName).isEmpty();
        boolean checkValuePresenceInJson = jsonNode.findValuesAsText(jsonFieldName).contains(searchingValue);

        return (checkJsonObjFieldNamePresence)? (checkValuePresenceInJson) : false;
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
                case "scheduleTask":
                    if(updatedValue instanceof Task) {
                        addTaskToSchedule(jsonNode, ((Task) updatedValue), pathToFile);
                    }
                    break;
                case "editTaskLibelle":
                    if(updatedValue instanceof String) {
                        editTaskLibelle(jsonNode, nodeValue, (String) updatedValue, pathToFile);
                    }
                    break;
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
        List<JsonNode> scheduleList = scheduleNode.findValues(TASK_FIELD);

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
            achievedNode = ((ArrayNode) achievedNode).add(taskCopy);
            System.out.println("achievedNode: " + achievedNode);

            if(isTaskAchieved(jsonNode, taskCopy)) {
                deleteScheduleTask(scheduleNode, searchingTask);
            }

            FileAnalyser.serializeFile(pathToFile, jsonNode);
        }
        else {
            System.out.println(String.format("%1$s \"%2$s\" is not in [schedule]", TASK_FIELD.toUpperCase(), taskName));
        }
    }


    private static void addTaskToSchedule(JsonNode jsonNode, Task taskToCreate, String pathToFile) throws IOException {
        JsonNode scheduleNode = getscheduleNode(jsonNode);
        String searchingValue = taskToCreate.getTaskName();

        if(JsonHandler.isSearchingValue_existsInNodeField(TASK_FIELD, searchingValue, scheduleNode)) {
            System.out.println(String.format("%1$s \"%2$s\" is already present in [schedule]", TASK_FIELD.toUpperCase(), searchingValue));
        }//todo: else if (case: where similarities in words matching scheduled task(s) to link with)
        else {
            //ObjectNode declaration to add new task in schedule
            ObjectNode scheduleTaskToAddNode = ((ArrayNode) scheduleNode).addObject();

            scheduleTaskToAddNode.put(TASK_FIELD, searchingValue);
            scheduleTaskToAddNode.put("priorityOrder", taskToCreate.getPriorityOrder());
            scheduleTaskToAddNode.putArray("activityTypes");
            scheduleTaskToAddNode.set("activityTypes", FileAnalyser.objMapper.valueToTree(taskToCreate.getActivityTypes()));
            scheduleTaskToAddNode.put("required", taskToCreate.isRequired());
            scheduleTaskToAddNode.put("completed", taskToCreate.isCompleted());

            FileAnalyser.serializeFile(pathToFile, jsonNode);
            System.out.println(String.format("New task scheduled: %s", scheduleTaskToAddNode));
        }
    }

    //todo:implement task editor feature "editTaskLibelle" & updateTask() update Whole Task/field(s)
    private static void editTaskLibelle(JsonNode jsonNode, String inputTasklibelle, String editedTaskLibelle, String pathToFile) throws IOException {
        JsonNode scheduleNode = getscheduleNode(jsonNode);

        JsonNode inputJsonNode = findTaskNode(inputTasklibelle, TASK_TYPE[0], jsonNode);
        if(inputJsonNode != null) {
            //System.out.println("ArrayNode: "+ scheduleNode.isArray());//todo: in custom BusinessException file if scheduleNode.isArray()
            int inputNode_index = scheduleNode.findValues(TASK_FIELD).indexOf(inputJsonNode);
            System.out.println(String.format("Current %1$s name will be edited from node: \n%2$s", TASK_FIELD, scheduleNode.get(inputNode_index)));

            JsonNode jsonNodeToEdit = scheduleNode.get(inputNode_index);
            jsonNodeToEdit = ((ObjectNode) jsonNodeToEdit).put("task", editedTaskLibelle);

            FileAnalyser.serializeFile(pathToFile, jsonNode);
            System.out.println(String.format("Final edited version: %1$s", jsonNodeToEdit));
        }
    }

    /**
     * Task Search depends on taskTypeInput value ({@code "schedule"}, {@code "achieved"})
     * and returns a {@code TextNode} value if {@code taskName} result found.
     * @return a {@code TextNode} value of {@code taskName} inside {@code JsonNode} type
     */
    public static JsonNode findTaskNode(String taskName, String taskTypeInput, JsonNode jsonNode) {
        JsonNode nodeResult = null;

        if(Arrays.asList(TASK_TYPE).contains(taskTypeInput)) {
            JsonNode taskTypeInputNode;
            taskTypeInputNode = (taskTypeInput.equals(TASK_TYPE[0])) ? getscheduleNode(jsonNode) : getachievedNode(jsonNode);
            //System.out.println(String.format("taskname to search: %s", taskName));/*keep to debug*/
            List<String> taskNodeList = taskTypeInputNode.findValuesAsText(TASK_FIELD);

            //Check if getscheduleNode() in "schedule" Case / getachievedNode() in "achieved" Case contains searching [taskName]
            if(taskNodeList.contains(taskName)) {
                int indexTask = taskNodeList.indexOf(taskName);
                System.out.println(String.format("%1$S \"%2$s\" found at indexTask: %3$s of [%4$s]", TASK_FIELD, taskNodeList.get(indexTask), indexTask, taskTypeInput));
                return taskTypeInputNode.findValues(TASK_FIELD).get(indexTask);//return a TextNode
            }
            System.err.println(String.format("%1$S \"%2$s\" not found in [%3$s]", TASK_FIELD, taskName, taskTypeInput));
            //todo:suggest results when similarities came (regex&str_deep_process)
        }
        else {//Error on [nodeOperation] parameter. Incompatible value given: nodeOperation is different from TASK_TYPE
            System.err.println(String.format("Error on [taskTypeInput] parameter. Wrong argument value is passed: %s", taskTypeInput));
        }
        return nodeResult;
    }
    private static JsonNode getscheduleNode(JsonNode jsonNode) { return jsonNode.get("schedule"); }
    private static JsonNode getachievedNode(JsonNode jsonNode) { return jsonNode.get("achieved"); }

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