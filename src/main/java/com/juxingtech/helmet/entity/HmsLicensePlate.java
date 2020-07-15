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
public class HmsLicensePlate implements Serializable {

    @TableId
    private Long id;

    private String plateNumber;

    private String plateColor;

    private String alarmContent;

}