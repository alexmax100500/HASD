package com.hasd.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NamesSerializer {
    public byte[] serializeRows(List<String> rows) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        List<Byte> bytesList = new ArrayList<>();
        try {
            for (String row : rows) {
                String[] parts = row.split(" ");
                String type = parts[1];
                String value = parts[0];

                byte fieldType = getFieldTypeByte(type);
                dataOutputStream.writeByte(fieldType);

                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                byte[] lengthBytes = encodeVLQ(valueBytes.length);
                dataOutputStream.write(lengthBytes);
                dataOutputStream.write(valueBytes);
            }

            // EOF byte
            dataOutputStream.writeByte(0x1C);
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] encodeVLQ(int value) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        do {
            byte encodedByte = (byte) (value & 0x7F);
            value >>>= 7;

            if (value != 0) {
                encodedByte |= 0x80;
            }

            byteArrayOutputStream.write(encodedByte);
        } while (value != 0);

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
