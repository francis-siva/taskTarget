package com.codestudiocorp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.FileReader;
import java.io.IOException;

public class FileAnalyser {
    public static final ObjectMapper objMapper = new ObjectMapper();

    public static void readFile(String pathToFile) {

        try {
            FileReader fileReader = new FileReader(pathToFile);

            JsonNode jsonNode = objMapper.readTree(fileReader);
            //System.out.println(jsonNode.toString());
            //System.out.println(jsonNode.size());

            //jsonNode.elements().forEachRemaining(jnode -> System.out.println(jnode.getNodeType() + " " + jnode));
            System.out.println();

            jsonNode.fieldNames().forEachRemaining(field -> {
                System.out.println("fieldName: " + field);
                JsonNodeType jNodeType = jsonNode.get(field).getNodeType();
                System.out.println((jNodeType == JsonNodeType.ARRAY) ? "Values: " + jsonNode.get(field).toPrettyString() : "Value: " + jsonNode.findValues(field));
            });

            fileReader.close();
        }
        catch (IOException ioe) {
            System.err.println("error cause: " + ioe.getMessage());
            ioe.printStackTrace();
            System.exit(-1);
        }
        catch (Exception e) {
            System.err.println("error cause: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
