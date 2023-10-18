package com.hasd.demo.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        SaveServiceImpl saveService = new SaveServiceImpl();
        JsonParser parser = new JsonParser();

        String json = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\", \"call\": {\"number\":123}} }";
        ObjectMapper mapper = new ObjectMapper();
        List<String> fieldValues = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();
        NamesSerializer serializer = new NamesSerializer();
        NamesDeserializer deserializer = new NamesDeserializer();
        try {
            // Parse JSON into a JsonNode
            JsonNode jsonNode = mapper.readTree(json);
            // Extract field names and values
            saveService.storeFieldNamesAndValues(jsonNode, "obj", "/home/cunning/studying/hasd/HASD/", fieldValues,
                    fieldNames);
            saveService.writeToFile("/home/cunning/studying/hasd/HASD/fieldNames", fieldNames);
            saveService.writeToFile("/home/cunning/studying/hasd/HASD/fieldValues", fieldValues);

            JsonNode node = parser.parseToJSON(fieldNames, fieldValues);
            System.out.println(mapper.writeValueAsString(node));
            byte[] namesBytes = serializer.serializeRows(fieldNames);
            System.out.println(serializer.bytesToHex(namesBytes));
            String[] strings = deserializer.deserializeRows(namesBytes);
            for (String s : strings) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
