package com.juxingtech.helmet.common.enums;

public enum HttpEnum {
    GET(1,"get"),
    POST(2,"post"),
    PUT(3,"put"),
    DELETE(4,"delete");
    private int num;
    private String desc;


    HttpEnum(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }
    public int getNum() {
        return num;
    }

    public String getDesc() {
        return desc;
    }

}
