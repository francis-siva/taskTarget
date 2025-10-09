package com.codestudiocorp.controllers;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonHandler {
    /**
     * To search if a particular value exists in a given Json Object fieldname.</br>
     * e.g. Searching for current available task element before operating on its index
     * @param jsonFieldName
     * @param searchingValue
     * @param jsonNode
     * @return
     */
    public static boolean isSearchingValue_existsInNodeField(String jsonFieldName, String searchingValue, JsonNode jsonNode) {
        boolean res = false;
        boolean checkJsonObjFieldNamePresence = jsonNode.findValuesAsText(jsonFieldName).size() > 0 && !jsonNode.findValuesAsText(jsonFieldName).isEmpty();
        boolean checkValuePresenceInJson = jsonNode.findValuesAsText(jsonFieldName).contains(searchingValue);

        if(checkJsonObjFieldNamePresence) {
            return (checkValuePresenceInJson);
        }
        return res;
    }

    public void setNodeValue(String jsonFieldName, String updatedValue, JsonNode jsonNode) {

    }
}
