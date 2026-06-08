package com.codestudiocorp;

import com.codestudiocorp.controllers.JsonHandler;
import com.codestudiocorp.exceptions.IllegalBusinessNodeException;
import com.codestudiocorp.utils.ExceptionMessagePrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileAnalyser {
    public static final ObjectMapper objMapper = new ObjectMapper();
    public static final ArrayList<String> FILE_REQUIRED_FIELDS = new ArrayList<>(Arrays.asList("priority", "activityScope", "schedule", "achieved"));

    public static boolean isJsonFileExtension(String pathToFile) {

        File file = new File(pathToFile);
        //System.out.println(file.getName());
        List<String> filenameParts = Arrays.asList(file.getName().split("\\."));
        return "json".equals(filenameParts.get(filenameParts.size()-1));
    }

    /**
     * Search presence of required Fieldnames in file
     * @param pathToFile
     * @return
     * @throws IOException
     */
    public static boolean requiredFields_areInFile(String pathToFile) throws IOException, IllegalBusinessNodeException {
        boolean res = false;

        if(isJsonFileExtension(pathToFile)) {
            FileReader fileReader = new FileReader(pathToFile);
            JsonNode jsonNode = objMapper.readTree(fileReader);

            //System.out.println("filterCollected?" + FILE_REQUIRED_FIELDS.stream().filter(fieldName -> jsonNode.has(fieldName)).collect(Collectors.toList()).size());
            res = FILE_REQUIRED_FIELDS.stream().allMatch(fieldName -> jsonNode.has(fieldName));
            System.out.println(String.format("Required_Fields allMatch? %1b", res));

            if (!res)
                throw new IllegalBusinessNodeException("Required field(s) missing in current file");
        }

        return res;
    }

    public static void readFile(String pathToFile) {

        try {
            if(requiredFields_areInFile(pathToFile)) {
                FileReader fileReader = new FileReader(pathToFile);

                JsonNode jsonNode = objMapper.readTree(fileReader);
                //System.out.println(jsonNode.toString());
                //System.out.println(jsonNode.size());

                //jsonNode.elements().forEachRemaining(jnode -> System.out.println(jnode.getNodeType() + " " + jnode));System.out.println();

                jsonNode.fieldNames().forEachRemaining(field -> {
                    System.out.println("fieldName: " + field);
                    JsonNodeType jNodeType = jsonNode.get(field).getNodeType();
                    System.out.println((jNodeType == JsonNodeType.ARRAY) ? "Values: " + jsonNode.get(field).toPrettyString() : "Value: " + jsonNode.findValues(field));
                });

                fileReader.close();
            }
        }
        catch (IOException ioe) {
            ExceptionMessagePrinter exceptMessagePrinter = new ExceptionMessagePrinter(ioe);
            exceptMessagePrinter.showErrorAndCause();
            System.exit(-1);
        }
        catch (Exception e) {
            ExceptionMessagePrinter exceptMessagePrinter = new ExceptionMessagePrinter(e);
            exceptMessagePrinter.showErrorAndCause();
            System.exit(-1);
        }
    }

    public static void writeFile(String pathToFile) {

        try {
            if(requiredFields_areInFile(pathToFile)) {
                FileReader fileReader = new FileReader(pathToFile);

                JsonNode jsonNode = objMapper.readTree(fileReader);
                JsonHandler.setNodeValue("repair product", true, jsonNode, "setTaskCompleted", pathToFile);
                JsonHandler.setNodeValue("repair product", null, jsonNode, "setTaskAchieved", pathToFile);

                fileReader.close();
            }
        }
        catch (IOException | IllegalBusinessNodeException e) {
            ExceptionMessagePrinter exceptMessagePrinter = new ExceptionMessagePrinter(e);
            exceptMessagePrinter.showErrorAndCause();
            System.exit(-1);
        }
    }

    public static void serializeFile(String pathToFile, JsonNode jsonNode) throws IOException {
        objMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objMapper.writeValue(new File(pathToFile), jsonNode);
    }
}
