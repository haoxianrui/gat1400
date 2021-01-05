package com.youlai.gat1400.domain;

import lombok.Data;

import java.util.List;

@Data
public class MotorVehicleRequestObject {

    private MotorVehicleListObject MotorVehicleListObject;

    @Data
    public static class MotorVehicleListObject {
        private List<MotorVehicle> MotorVehicleObject;
    }

    @Data
    public static class MotorVehicle {
        private String MotorVehicleID;
        private Integer InfoKind;
        private String SourceID;
        private String DeviceID;
        private String StorageUrl1;
        private Integer LeftTopX;
        private Integer LeftTopY;
        private Integer RightBtmX;
        private Integer RightBtmY;
        private Boolean HasPlate;
        private String PlateClass;
        private String PlateColor;
        private String PlateNo;
        private String VehicleColor;
        private SubImageList SubImageList;
    }

    @Data
    public static class SubImageList {
        private List<SubImageInfo> SubImageInfoObject;
    }
}
