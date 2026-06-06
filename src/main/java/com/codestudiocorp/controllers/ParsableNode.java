package com.codestudiocorp.controllers;

import com.fasterxml.jackson.databind.JsonNode;

public interface ParsableNode {
    JsonNode getNode(JsonNode node);
}
