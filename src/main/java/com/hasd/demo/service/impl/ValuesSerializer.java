package com.hasd.demo.service.impl;

import java.nio.ByteBuffer;
import java.util.List;

public class ValuesSerializer {
    public byte[] serializeRows(List<String> values, List<String> scheme) {
        int valuesIndex = 0;
        int schemeIndex = 0;
        byte[] appendBytes;
        while (valuesIndex < values.size()) {
            appendBytes = getBytes(values.get(valuesIndex), Type.valueOf(Meta.getType(scheme.get(schemeIndex))));
        }
        return null;
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
    }

    private byte[] getIntBytes(String value) {
        int intValue = Integer.parseInt(value);
        return zigZag(intValue);
    }

    private byte[] zigZag(int value) {
        int zigzagEncodedValue = (value << 1) ^ (value >> 31);
        return ByteBuffer.allocate(4).putInt(zigzagEncodedValue).array();
    }

    private  byte[] getObjectBytes(String value) {
    }

    private byte[] getStringBytes(String value) {
        return value.getBytes();
    }

    private byte[] getFloatBytes(String value) {
    }
}
