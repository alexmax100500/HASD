package com.hasd.demo.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws IOException {
        int am = 50;
        String json = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\", \"call\": {\"number\":1.3";
        List<String> jsonList = new ArrayList<>();
        for (int i = 0; i < am; i++){
            if( i == 68){
                continue;
            }
            jsonList.add(json + i + "}}");
        }
        Worker worker = new Worker();
        int hash = worker.saveBatch(jsonList);
        System.out.println(hash);
        List<String> jsonString = worker.readBatch(String.valueOf(hash), am);
    }
}
