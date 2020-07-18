package com.juxingtech.helmet.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haoxr
 * @date 2020-07-03
 **/

@ApiModel
@Data
public class LicensePlateResp {

    private int status;

    private String hphm;

    private String hpys;

    private String clpp;

    private String csys;

    private String clzt;

    private String cllx;

    private String syxz;

    private int type;


}
