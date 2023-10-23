package com.hasd.demo.service.impl;

public enum Type {
    STRING("STRING"),
    INTEGER("INTEGER"),
    OBJECT("OBJECT"),
    FLOAT("FLOAT");
    public final String type;

    private Type(String type) {
        this.type = type;
    }
}
