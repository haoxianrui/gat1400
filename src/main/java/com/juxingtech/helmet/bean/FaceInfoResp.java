package com.juxingtech.helmet.bean;

import lombok.Data;

import java.util.List;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class FaceInfoResp {

    private String event;
    private String channelId;
    private String channelCode;
    private String channelName;
    private double gpsX;
    private double gpsY;
    private String deviceId;
    private String gbCode;
    private String uid;
    private String faceImgId;
    private String faceImgUrl;
    private String imgUrl;
    private String faceImgUrlEx;
    private String imgUrlEx;
    private String alarmCode;
    private String alarmId;
    private String faceRecordId;
    private String recordId;
    private double capTime;
    private int alarmTime;
    private int faceLeft;
    private int faceTop;
    private int faceRight;
    private int faceBottom;
    private String extRecordId;
    private String extParam;
    private int dataSource;
    private int age;
    private int gender;
    private int race;
    private String ethnicCode;
    private int fringe;
    private int eye;
    private int mouth;
    private int beard;
    private int mask;
    private int glasses;
    private int emotion;
    private List<SimilarFacesBean> similarFaces;

    @Data
    public static class SimilarFacesBean {
        private String targetFaceImgId;
        private String targetFaceImgUrl;
        private String targetImgUrl;
        private double similarity;
        private String repositoryId;
        private String repositoryName;
        private String idNumber;
        private int idType;
        private String passportType;
        private String name;
        private String birthday;
        private String ethnicCode;
        private int gender;
    }
}
