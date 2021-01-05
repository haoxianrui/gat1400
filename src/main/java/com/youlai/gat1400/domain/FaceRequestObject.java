package com.youlai.gat1400.domain;

import lombok.Data;

import java.util.List;

@Data
public class FaceRequestObject {

    private FaceListObject FaceListObject;

    @Data
    public static class FaceListObject {
        private List<Face> FaceObject;
    }

    @Data
    public static class Face {
        private String FaceID;
        private Integer InfoKind;
        private String SourceID;
        private String DeviceID;
        private Integer LeftTopX;
        private Integer LeftTopY;
        private Integer RightBtmX;
        private Integer RightBtmY;
        private Integer IsDriver;
        private Integer IsForeigner;
        private Integer IsSuspectedTerrorist;
        private Integer IsCriminalInvolved;
        private Integer IsDetainees;
        private Integer IsVictim;
        private Integer IsSuspiciousPerson;
        private SubImageList SubImageList;
    }

    @Data
    public static class SubImageList {
        private List<SubImageInfo> SubImageInfoObject;
    }

}
