package com.juxingtech.helmet.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haoxr
 * @date 2020-07-03
 **/

@ApiModel
@Data
public class SubscribeReq {


    @ApiModelProperty(value = "设备ID", example = "13030421191190201061", required = true)
    private String deviceId;


}
