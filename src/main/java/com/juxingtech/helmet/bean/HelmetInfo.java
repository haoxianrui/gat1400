package com.juxingtech.helmet.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author haoxr
 * @date 2020-07-05
 **/
@ApiModel
@Data
public class HelmetInfo {

    @ApiModelProperty(value="头盔序列号",example="123456789",dataType = "java.lang.String",required = true)
    private String serialNo;

    @ApiModelProperty(value = "电量",example = "50%",required = true)
    private String electricQuantity;

    @ApiModelProperty(value = "头盔经度",example = "121.446278",required = true)
    private BigDecimal lng;

    @ApiModelProperty(value = "头盔纬度",example = "31.224939",required = true)
    private BigDecimal lat;


}
