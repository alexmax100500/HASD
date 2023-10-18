package com.hasd.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NamesSerializer {
public byte[] serializeRows(List<String> rows) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

    try {
        for (String row : rows) {
                String[] parts = row.split(" ");
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
        dataOutputStream.writeByte(0x00);
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

    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
