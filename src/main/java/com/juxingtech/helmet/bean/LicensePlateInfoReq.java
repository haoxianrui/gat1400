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
public class LicensePlateInfoReq {

    @ApiModelProperty(value = "设备ID", example = "230102999011190100011", required = true)
    private String deviceId;

    @ApiModelProperty(value = "车牌图片(base64编码格式)", example = "1", required = true)
    private String image;

    @ApiModelProperty(value = "左上角X坐标", example = "100", required = true)
    private Integer leftTopX;

    @ApiModelProperty(value = "左上角Y坐标", example = "100", required = true)
    private Integer leftTopY;

    @ApiModelProperty(value = "右下角X坐标", example = "100", required = true)
    private Integer rightBtmX;

    @ApiModelProperty(value = "右下角X坐标", example = "100", required = true)
    private Integer rightBtmY;

    @ApiModelProperty(value = "车牌图片宽度", example = "200", required = true)
    private Integer width;

    @ApiModelProperty(value = "车牌图片高度", example = "200", required = true)
    private Integer height;

    @ApiModelProperty(value = "车牌颜色", example = "蓝", required = true)
    private String plateColor;

    @ApiModelProperty(value = "车牌号", example = "沪A88888", required = true)
    private String plateNo;
}
