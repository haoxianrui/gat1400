package com.dahua.device.common.enums;

public enum PlateColorEnum {

    BLACK(1, "黑"),
    WHITE(2, "白"),
    GREY(3, "灰"),
    RED(4, "红"),
    BLUE(5, "蓝"),
    YELLOW(6, "黄"),
    ORANGE(7, "橙"),
    PALM(8, "棕"),
    GREEN(9, "绿"),
    PURPLE(10, "紫"),
    CYAN(11, "青"),
    PINK(12, "粉"),
    TRANSPARENT(13, "透明"),
    OTHER(99, "其他");


    PlateColorEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;

    private String name;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByCode(Integer code){
        PlateColorEnum[] values = PlateColorEnum.values();
        for(PlateColorEnum plateColorEnum:values){
            if(code.equals(plateColorEnum.getCode())){
                return plateColorEnum.getName();
            }
        }
        return null;
    }

    public static String getCodeByName(String name){
        PlateColorEnum[] values = PlateColorEnum.values();
        for(PlateColorEnum plateColorEnum:values){
            if(name.equals(plateColorEnum.getName())){
                return String.valueOf(plateColorEnum.getCode());
            }
        }
        return null;
    }

}
