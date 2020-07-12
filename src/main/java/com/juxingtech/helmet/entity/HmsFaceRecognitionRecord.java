package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Data
public class HmsFaceRecognitionRecord {

    @TableId
    private long id;

    private String name;

    private int gender;

    private double score;

    private String imgUrl;

    private String alarmContent;

    private Date alarmTime;

    private String helmetSerialNo;

    private Date createTime;
}
