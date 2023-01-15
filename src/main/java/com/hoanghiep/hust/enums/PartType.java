package com.hoanghiep.hust.enums;

public enum PartType {

    READING("READING"), LISTENING("LISTENING");

    private String value;

    PartType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
