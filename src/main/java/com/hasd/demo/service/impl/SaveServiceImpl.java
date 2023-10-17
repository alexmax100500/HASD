package com.hasd.demo.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SaveServiceImpl {
    public static void writeToFile(String fileName, String content) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.append(content);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as per your requirement
        }
    }
    public static void storeFieldNamesAndValues(JsonNode node, String parentPath, String outputPath) {
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                String fieldName = entry.getKey();
                String fieldType = entry.getValue().getNodeType().name();
                String fieldPath = parentPath + "." + fieldName;

                writeToFile(outputPath + "fieldNames.txt", fieldName);
                writeToFile(outputPath + "fieldValues.txt", entry.getValue().asText());

                storeFieldNamesAndValues(entry.getValue(), fieldPath, outputPath);
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                JsonNode element = node.get(i);
                String elementPath = parentPath + "[" + i + "]";

                storeFieldNamesAndValues(element, elementPath, outputPath);
            }
        }
    }
    public static String getFieldNames(JsonNode node, String parentPath) {
        StringBuilder fieldNames = new StringBuilder();

        if (node.isObject()) {
            // Iterate over the fields of the object
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                String fieldName = entry.getKey();
                String fieldType = entry.getValue().getNodeType().name();
                String fieldPath = parentPath + "." + fieldName;

                fieldNames.append(fieldPath).append(" (").append(fieldType).append("), ");

                // Recursively call getFieldNames() for nested objects
                fieldNames.append(getFieldNames(entry.getValue(), fieldPath));
            }
        } else if (node.isArray()) {
            // Iterate over the elements of the array
            for (int i = 0; i < node.size(); i++) {
                JsonNode element = node.get(i);
                String elementPath = parentPath + "[" + i + "]";

                // Recursively call getFieldNames() for each array element
                fieldNames.append(getFieldNames(element, elementPath));
            }
        }

        return fieldNames.toString();
    }

    public static void main(String[] args) {

        String json = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\",\"pets\":[{\"name\":\"Fluffy\",\"type\":\"cat\"} , {\"salt\":4465}], \"call\": 123 }";
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse JSON into a JsonNode
            JsonNode jsonNode = mapper.readTree(json);
            // Extract field names and values
            Map<String, String> fieldValues = new HashMap<>();
            storeFieldNamesAndValues(jsonNode, "obj", "/home/alexmax/");
//            for (Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields(); it.hasNext(); ) {
//                Map.Entry<String, JsonNode> field = it.next();
//                String fieldName = field.getKey();
//                System.out.println("fieldName = " + fieldName);
//                String fieldValue = field.getValue().asText();
//                fieldValues.put(fieldName, fieldValue);
//            }

            // Save field names and values separately
//            saveFieldNames(fieldValues.keySet());
//            saveFieldValues(fieldValues);

        } catch (IOException e) {
            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }
    }

    public void parse() {
        String jsonString = "{ \"field1\": \"value1\", \"field2\": \"value2\" }";
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse JSON into a JsonNode
            JsonNode jsonNode = mapper.readTree(jsonString);

            // Extract field names and values
            Map<String, String> fieldValues = new HashMap<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> field = it.next();
                String fieldName = field.getKey();
                String fieldValue = field.getValue().asText();
                fieldValues.put(fieldName, fieldValue);
            }

            // Save field names and values separately
//            saveFieldNames(fieldValues.keySet());
//            saveFieldValues(fieldValues);

        } catch (IOException e) {
            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        }
    }
}
