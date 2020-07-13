package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Data
public class HmsMotorVehicleRecord {

    @TableId
    private long id;

    private String number;

    private String color;

    private String imgUrl;

    private String alarmContent;

    private Date alarmTime;

    private String deviceId;

    private Date createTime;

    private Integer type;
}
