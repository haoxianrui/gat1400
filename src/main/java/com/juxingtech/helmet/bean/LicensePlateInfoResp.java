package com.juxingtech.helmet.bean;

import lombok.Data;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class LicensePlateInfoResp {

    private long alarmTime;
    private long capTime;
    private String channelCode;
    private String channelId;
    private String channelName;
    private long createTime;
    private String event;
    private String imgUrl1;
    private String imgUrl2;
    private String objId;
    private String objRecordId;
    private int objType;
    private String recordId;
    private int recordType;
    private int stat;
    private String surveyRecordId;
    private int surveySource;
    private int surveyType;
    private String tagCode;
    private String userChannelCode;

}
