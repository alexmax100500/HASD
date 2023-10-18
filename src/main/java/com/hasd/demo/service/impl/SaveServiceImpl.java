package com.hasd.demo.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SaveServiceImpl {
    public  void writeToFile(String fileName, List<String> content) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            for (String s : content)
                fileWriter.append(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as per your requirement
        }
    }

    public  void storeFieldNamesAndValues(JsonNode node, String parentPath, String outputPath, List<String> fieldValues, List<String> fieldNames) {
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                String fieldName = entry.getKey();
                String fieldType = entry.getValue().getNodeType().name();
                String fieldPath = parentPath + "." + fieldName;
                String fieldValue = entry.getValue().asText();
                if (fieldType.equals("NUMBER")) {
                        try {
                            Integer.parseInt(fieldValue);
                            fieldType = "INTEGER";
                        } catch (NumberFormatException e) {
                            try {
                                Float.parseFloat(fieldValue);
                                fieldType = "FLOAT";
                            } catch (NumberFormatException e1) {
                                throw new NumberFormatException(fieldValue);
                            }
                        }
                }
                fieldNames.add(fieldName + " " + fieldType);
                if (fieldType == "OBJECT") {
                    fieldValues.add("Start of object");
                } else  if(fieldType!="ARRAY"){
                    fieldValues.add(fieldValue);
                }

                storeFieldNamesAndValues(entry.getValue(), fieldPath, outputPath, fieldValues, fieldNames);
                if (fieldType == "OBJECT") {
                    fieldValues.add("End of object");
                }
            }
        }
    }

    public  JsonNode restoreJsonFromFieldNamesAndValues(List<String> fieldNames, List<String> fieldValues) {
        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        ObjectNode rootNode = jsonNodeFactory.objectNode();
        // Stack to keep track of parent nodes
        Deque<JsonNode> parentStack = new ArrayDeque<>();
        parentStack.push(rootNode);
        int fieldNamesPointer = 0;
        for (int i = 0; i < fieldValues.size(); i++) {
            String fieldName = fieldNames.get(fieldNamesPointer).split(" ")[0];
            String fieldType = fieldNames.get(fieldNamesPointer).split(" ")[1];
            String fieldValue = fieldValues.get(i);

            if (fieldValue.equals("Start of object")) {
                // continue;
                // Create a new object node
                ObjectNode objectNode = jsonNodeFactory.objectNode();
                // Add the object node to the parent node
                ((ObjectNode) parentStack.peek()).set(fieldValue, objectNode);
                // Push the new object node to the stack
                parentStack.push(objectNode);
            } else if (fieldValue.equals("End of object")) {
                // Pop the current object node from the stack
                parentStack.pop();
            } else {
                // Add the field name and value to the current object node
                ((ObjectNode) parentStack.peek()).set(fieldName, jsonNodeFactory.textNode(fieldValue));
            }
        }

        return rootNode;
    }

    public void parse() {
        String jsonString = "{ \"field1\": \"value1\", \"field2\": \"value2\" }";
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse JSON into a JsonNode
            JsonNode jsonNode = mapper.readTree(jsonString);

            // Extract field names and values
            Map<String, String> fieldValues = new HashMap<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> field = it.next();
                String fieldName = field.getKey();
                String fieldValue = field.getValue().asText();
                fieldValues.put(fieldName, fieldValue);
            }

            // Save field names and values separately
            // saveFieldNames(fieldValues.keySet());
            // saveFieldValues(fieldValues);

        } catch (IOException e) {
            e.printStackTrace();
            // } catch (JsonMappingException e) {
            // throw new RuntimeException(e);
            // } catch (JsonProcessingException e) {
            // throw new RuntimeException(e);
            // }
        }
    }
}
