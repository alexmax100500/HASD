package com.hasd.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class ValuesSerializer {
    public byte[] serializeRows(List<String> values, List<String> scheme) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int valuesIndex = 0;
        int schemeIndex = 0;
        byte[] appendBytes;
        while (valuesIndex < values.size()) {
            if(values.get(valuesIndex).equals("End of object")){
                byte b = 0x1D;
                appendBytes = ByteBuffer.allocate(1).put(b).array();
                baos.write(appendBytes);
                baos.write(0x1D);
                valuesIndex++;
                continue;
            }

            Type type = Type.valueOf(Meta.getType(scheme.get(schemeIndex)));
            appendBytes = getBytes(values.get(valuesIndex), type);
            baos.write(appendBytes);
            baos.write(0x1D);
            valuesIndex++;
            schemeIndex++;
        }
        baos.write(0x1C);
        return baos.toByteArray();
    }

    private byte[] getBytes(String value, Type type) {
        switch (type){
            case FLOAT:
                return getFloatBytes(value);
            case OBJECT:
                return getObjectBytes(value);
            case STRING:
                return getStringBytes(value);
            case INTEGER:
                return getIntBytes(value);
        }
        return null;
    }

    private byte[] getIntBytes(String value) {
        int intValue = Integer.parseInt(value);
        return zigZag(intValue);
    }

    private byte[] zigZag(int value) {
        int zigzagEncodedValue = (value << 1) ^ (value >> 31);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        do {
            byte b = (byte) (zigzagEncodedValue & 0x7F);
            zigzagEncodedValue >>= 7;
            if (zigzagEncodedValue != 0) {
                b |= 0x80;
            }
            baos.write(b);
        } while (zigzagEncodedValue != 0);

        return baos.toByteArray();
    }

    private  byte[] getObjectBytes(String value) {
        return new byte[]{0x1C};
    }

    private byte[] getStringBytes(String value) {
        return value.getBytes();
    }

    private byte[] getFloatBytes(String value) {
        float floatValue = Float.parseFloat(value);
        return ByteBuffer.allocate(4).putFloat(floatValue).array();
    }
}
