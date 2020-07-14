package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hxrui
 * @date 2020-07-14
 */
@ApiModel
@Data
public class HmsHelmet implements Serializable {

    @TableId
    private Long id;

    private String name;

    private String serialNo;

    private String deviceId;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String electricQuantity;

    private Integer status;

    @TableField(exist = false)
    private Integer onlineStatus;
}