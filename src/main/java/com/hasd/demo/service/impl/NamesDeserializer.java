package com.hasd.demo.service.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NamesDeserializer {
    public String[] deserializeRows(byte[] serializedRows) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedRows);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        StringBuilder rowBuilder = new StringBuilder();
        String[] rows = new String[0];

        try {
            while (dataInputStream.available() > 0) {
                byte fieldType = dataInputStream.readByte();
                if (fieldType == (byte) 0x1C) {
                    return rows;
                }

                int valueLength = decodeVLQ(dataInputStream);

                byte[] valueBytes = new byte[valueLength];
                dataInputStream.readFully(valueBytes);

                String value = new String(valueBytes, StandardCharsets.UTF_8);

                switch (fieldType) {
                    case 0x01:
                        rowBuilder.append(value).append(" STRING");
                        break;
                    case 0x02:
                        rowBuilder.append(value).append(" INTEGER");
                        break;
                    case 0x03:
                        rowBuilder.append(value).append(" OBJECT");
                        break;
                    case 0x04:
                        rowBuilder.append(value).append(" FLOAT");
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid field type byte: " + fieldType);
                }
                rows = appendRow(rows, rowBuilder.toString());
                rowBuilder.setLength(0);
            }
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

    private int decodeVLQ(DataInputStream dataInputStream) throws IOException {
        int value = 0;
        int shift = 0;

        byte b;
        do {
            b = dataInputStream.readByte();
            int byteValue = b & 0xFF;
            value |= (byteValue & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);

        return value;
    }

    public String[] appendRow(String[] array, String element) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }
}