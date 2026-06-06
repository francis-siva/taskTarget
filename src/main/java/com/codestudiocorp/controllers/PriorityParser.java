package com.codestudiocorp.controllers;

import com.fasterxml.jackson.databind.JsonNode;

import static com.codestudiocorp.FileAnalyser.FILE_REQUIRED_FIELDS;

public class PriorityParser implements ParsableNode {
    @Override
    public JsonNode getNode(JsonNode node) {
        int attr_index = FILE_REQUIRED_FIELDS.indexOf("priority");
        return node.get(FILE_REQUIRED_FIELDS.get(attr_index));
    }
}
