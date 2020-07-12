package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hxrui
 * @since 1.0.0
 */
@ApiModel
@Data
public class HmsHelmet implements Serializable {

    @TableId
    private Long id;

    private String name;

    private String serialNo;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String electricQuantity;

    @TableField(exist = false)
    private Integer status;

}