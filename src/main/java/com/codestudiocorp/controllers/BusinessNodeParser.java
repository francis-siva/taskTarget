package com.codestudiocorp.controllers;

import com.codestudiocorp.exceptions.IllegalBusinessNodeException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import static com.codestudiocorp.FileAnalyser.FILE_REQUIRED_FIELDS;

@Data
public class BusinessNodeParser implements ParsableNode {
    private String nodeName;

    public BusinessNodeParser (String nodeName) throws IllegalBusinessNodeException {
        if(!FILE_REQUIRED_FIELDS.contains(nodeName)) {
            throw new IllegalBusinessNodeException("Invalid value passed in constructor");
        }
        else {
            this.nodeName = nodeName;
        }
    }
    @Override
    public JsonNode getNode(JsonNode node) {
        return node.get(this.nodeName);
    }
}
