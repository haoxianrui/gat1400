package com.juxingtech.helmet.common.enums;

/**
 * 数据源枚举
 */
public enum DataSourceTypeEnum {

    mysql("mysql"),oracle("oracle");

    private String value;

    DataSourceTypeEnum(String value){
        this.value=value;
    }

    public String getValue(){
        return value;
    }
}
