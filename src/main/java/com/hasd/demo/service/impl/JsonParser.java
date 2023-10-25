package com.hasd.demo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class JsonParser {
    public ObjectNode parseToJSON(List<String> fieldNames, List<String> fieldValues) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();

        Deque<JsonNode> parentStack = new ArrayDeque<>();
        parentStack.push(rootNode);

        int namesIndex = 0;
        int valuesIndex = 0;
        while (valuesIndex < fieldValues.size() && namesIndex < fieldNames.size()) {
            String fieldName = fieldNames.get(namesIndex).split(" ")[0];
            String fieldType = fieldNames.get(namesIndex).split(" ")[1];
            String fieldValue = fieldValues.get(valuesIndex);

            if (fieldValue.equals("Start of object")) {
                ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
                // Add the object node to the parent node
                ((ObjectNode) parentStack.peek()).set(fieldName, objectNode);
                // Push the new object node to the stack
                parentStack.push(objectNode);
                valuesIndex++;
                namesIndex++;
            } else if (fieldValue.equals("End of object")) {
                parentStack.pop();
                valuesIndex++;
            } else {
                if (fieldType.equals("INTEGER")) {
                    ((ObjectNode) parentStack.peek()).put(fieldName, Integer.parseInt(fieldValue));
                } else if (fieldType.equals("FLOAT")) {
                    ((ObjectNode) parentStack.peek()).put(fieldName, Float.parseFloat(fieldValue));
                } else {
                    ((ObjectNode) parentStack.peek()).put(fieldName, fieldValue);
                }
                valuesIndex++;
                namesIndex++;
            }
        }

        return rootNode;
    }

    public static byte[] serializeRows(String... rows) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            for (String row : rows) {
                String[] fields = row.split("\n");

                for (String field : fields) {
                    String[] parts = field.split(" ");
                    String type = parts[1];
                    String value = parts[0];

                    // Serialize the type as a single byte
                    byte fieldType = getFieldTypeByte(type);
                    dataOutputStream.writeByte(fieldType);

                    // Serialize the value as bytes
                    byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                    dataOutputStream.writeInt(valueBytes.length);
                    dataOutputStream.write(valueBytes);
                }

                // Add a separator byte between rows
                dataOutputStream.writeByte(0xFF);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static byte getFieldTypeByte(String type) {
        switch (type) {
            case "STRING":
                return 0x01;
            case "INTEGER":
                return 0x02;
            case "OBJECT":
                return 0x03;
            case "FLOAT":
                return 0x04;
            default:
                throw new IllegalArgumentException("Invalid field type: " + type);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static String[] deserializeRows(byte[] serializedRows) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedRows);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        StringBuilder rowBuilder = new StringBuilder();
        String[] rows = new String[0];

        try {
            while (dataInputStream.available() > 0) {
                byte fieldType = dataInputStream.readByte();
                int valueLength = dataInputStream.readInt();
                byte[] valueBytes = new byte[valueLength];
                dataInputStream.readFully(valueBytes);

                String value = new String(valueBytes, StandardCharsets.UTF_8);

                switch (fieldType) {
                    case 0x01:
                        rowBuilder.append(value).append(" STRING\n");
                        break;
                    case 0x02:
                        rowBuilder.append(value).append(" INTEGER\n");
                        break;
                    case 0x03:
                        rowBuilder.append(value).append(" OBJECT\n");
                        break;
                    case 0x04:
                        rowBuilder.append(value).append(" FLOAT\n");
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid field type byte: " + fieldType);
                }

                if (fieldType == (byte) 0xFF) {
                    rows = appendRow(rows, rowBuilder.toString());
                    rowBuilder.setLength(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public static String[] appendRow(String[] array, String element) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }
}
