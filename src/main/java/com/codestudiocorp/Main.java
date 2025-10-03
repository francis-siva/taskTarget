package com.codestudiocorp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    static final String JSON_FILE = "./src/main/resources/static/input/data.json";
    static final ObjectMapper objMapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("Task scheduler wip [=>]");


        try {
            JsonNode jsonNode = objMapper.readTree(new FileReader(JSON_FILE));
            System.out.println(jsonNode.toString());
            System.out.println(jsonNode.size());

            jsonNode.elements().forEachRemaining(jnode -> System.out.println(jnode.getNodeType() + " " + jnode));
            System.out.println();

            jsonNode.fieldNames().forEachRemaining(field -> {
                System.out.println("fieldName: " + field);
                JsonNodeType jNodeType = jsonNode.get(field).getNodeType();
                //System.out.println("NodeType: " + jNodeType);
                System.out.println((jNodeType == JsonNodeType.ARRAY) ? "Values: " + jsonNode.get(field).toPrettyString() : "Value: " + jsonNode.findValues(field));
            });

        }
        catch (IOException ioe) {
            System.err.println("error cause: " + ioe.getMessage());
            ioe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}