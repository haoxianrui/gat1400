package com.juxingtech.helmet.bean;

import lombok.Data;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class FaceInfo {

    private String similarity;

    private String gender;

    private String name;

    private String idCardNo;

    private String alarmContent;

    private String faceImgUrl;

}
