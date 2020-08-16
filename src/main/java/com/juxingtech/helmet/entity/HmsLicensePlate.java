package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class HmsLicensePlate {

    @TableId
    private Long id;

    private String username;

    private String plateNo;

    private Integer type;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

}
