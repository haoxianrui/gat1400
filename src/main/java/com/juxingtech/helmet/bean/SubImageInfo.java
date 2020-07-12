package com.juxingtech.helmet.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class SubImageInfo {

    @ApiModelProperty(value="图片ID【String(41)】",required = true,dataType = "string")
    private String ImageID;

    @ApiModelProperty(value = "视频图像分析处理事件类型",required = true,dataType = "int")
    private Integer EventSort;

    @ApiModelProperty(value = "设备编码【String(20)】", example = "", dataType = "string", required = true)
    private String DeviceID;

    @ApiModelProperty(value = "存储路径",required = true,dataType = "string")
    private String StoragePath;

    @ApiModelProperty(value = "图像类型",required = true,dataType = "string")
    private String ImageType;

    @ApiModelProperty(value = "图片格式",required = true,dataType = "string")
    private String FileFormat;

    @ApiModelProperty(value = "拍摄时间【格式(yyyyMMddHHmmss)】",required = true,dataType = "string")
    private String ShotTime;

    @ApiModelProperty(value = "水平像素值",required = true,dataType = "int")
    private Integer  Width;

    @ApiModelProperty(value = "垂直像素值",required = true,dataType = "int")
    private Integer Height;

    @ApiModelProperty(value = "图片（base64）",required = true,dataType = "string")
    private String Data;

    @ApiModelProperty(value = "图像类型（02-车牌彩色小图，11-人脸图）",dataType = "string")
    private String Type;

}
