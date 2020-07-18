package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Data
public class HmsFaceRecord {

    @TableId
    private Long id;

    private String name;

    private int gender;

    private double score;

    private String imgUrl;

    private String alarmContent;

    private Date alarmTime;

    private String deviceId;

    private Date createTime;

    private Integer type;

    private String idCardNo;

    private String targetImgUrl;

    @TableField(exist = false)
    private String helmetName;

    @TableField(exist = false)
    private String serialNo;

    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;
}
