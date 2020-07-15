package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hxrui
 * @date 2020-07-14
 */
@ApiModel
@Data
public class HmsNetworkConfig implements Serializable {

    @TableId(type= IdType.INPUT)
    private Integer id;

    private String serverIp;

    private String serverPort;

    private String userId;

    private String userPassword;

    @TableField(exist = false)
    private String imgDeviceId;

    @TableField(exist = false)
    private String videoDeviceId;
}