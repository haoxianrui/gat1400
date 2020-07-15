package com.juxingtech.helmet.controller.api;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.juxingtech.helmet.bean.*;
import com.juxingtech.helmet.common.constant.HelmetConstants;
import com.juxingtech.helmet.common.enums.PlateColorEnum;
import com.juxingtech.helmet.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(tags = "GAT/1400 机动车接口")
@RestController
@RequestMapping("/api/v1/motor-vehicles")
@Slf4j
public class MotorVehicleController {


    @Value(value = "${push-server.ip}")
    private String ip;

    @Value(value = "${push-server.port}")
    private Integer port;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation(value = "车牌信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "motorVehicleReq", value = "实体JSON对象", required = true, paramType = "body", dataType = "MotorVehicleReq")

    })
    public Result upload(
            @RequestBody MotorVehicleReq motorVehicleReq
    ) {
        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

        MotorVehicleRequestObject motorVehicleRequestObject=new MotorVehicleRequestObject();
        MotorVehicleRequestObject.MotorVehicleListObject motorVehicleListObject=new MotorVehicleRequestObject.MotorVehicleListObject();
        motorVehicleRequestObject.setMotorVehicleListObject(motorVehicleListObject);

        List<MotorVehicleRequestObject.MotorVehicle> motorVehicleList=new ArrayList<>();

        MotorVehicleRequestObject.MotorVehicle motorVehicle=new MotorVehicleRequestObject.MotorVehicle();
        motorVehicle.setInfoKind(1);  // 信息分类 0:其他 1:自动采集 2:人工采集

        // 图像信息基本要素ID
        String sourceIdPrefix = motorVehicleReq.getDeviceId() + "02" + time; // 02:图像
        String sourceIdOrderNumKey = HelmetConstants.REDIS_KEY_PREFIX_ORDER_NUM + sourceIdPrefix;
        Object sourceIdOrderNumVal = redisTemplate.opsForValue().get(sourceIdOrderNumKey);
        int sourceIdOrderNum = 0;
        if (sourceIdOrderNumVal != null) {
            sourceIdOrderNum = Integer.valueOf(sourceIdOrderNumVal.toString());
        }
        sourceIdOrderNum += 1;
        redisTemplate.opsForValue().set(sourceIdOrderNumKey, sourceIdOrderNum, 1, TimeUnit.SECONDS);
        String sourceIdOrderNumFormat = String.format("%05d", sourceIdOrderNum);
        String sourceId = sourceIdPrefix + sourceIdOrderNumFormat;
        motorVehicle.setSourceID(sourceId);  // 图像基本要素ID String(41)
        motorVehicle.setLeftTopX(motorVehicleReq.getLeftTopX());
        motorVehicle.setLeftTopY(motorVehicleReq.getLeftTopY());
        motorVehicle.setRightBtmX(motorVehicleReq.getRightBtmX());
        motorVehicle.setRightBtmY(motorVehicleReq.getRightBtmY());

        motorVehicle.setHasPlate(true);
        motorVehicle.setPlateClass("99"); // 号牌种类 99-其他号牌
        motorVehicle.setPlateColor(PlateColorEnum.getCodeByName(motorVehicleReq.getPlateColor())); // 车牌颜色
        motorVehicle.setPlateNo(motorVehicleReq.getPlateNo());
        motorVehicle.setVehicleColor("99"); // 车身颜色 99-其他

        // 图像信息内容要素ID
        String motorVehicleIdPrefix = sourceId + "02"; // 子类型编码 02：机动车
        String motorVehicleIdOrderNumKey = HelmetConstants.REDIS_KEY_PREFIX_ORDER_NUM + motorVehicleIdPrefix;
        Object motorVehicleIdOrderNumVal = redisTemplate.opsForValue().get(motorVehicleIdOrderNumKey);
        int motorVehicleIdOrderNum = 0;
        if (motorVehicleIdOrderNumVal != null) {
            motorVehicleIdOrderNum = Integer.valueOf(motorVehicleIdOrderNumVal.toString());
        }
        motorVehicleIdOrderNum += 1;
        redisTemplate.opsForValue().set(motorVehicleIdOrderNumKey, motorVehicleIdOrderNum, 1, TimeUnit.SECONDS);
        String motorVehicleIdOrderNumFormat = String.format("%05d", motorVehicleIdOrderNum);
        String motorVehicleId = motorVehicleIdPrefix + motorVehicleIdOrderNumFormat;
        motorVehicle.setMotorVehicleID(motorVehicleId); // 车牌ID String(48)
        
        motorVehicle.setDeviceID(motorVehicleReq.getDeviceId());// 设备ID

        // 图片子对象信息
        MotorVehicleRequestObject.SubImageList subImageList = new MotorVehicleRequestObject.SubImageList();
        SubImageInfo subImageInfo = new SubImageInfo();
        subImageInfo.setDeviceID(motorVehicleReq.getDeviceId());
        subImageInfo.setData(motorVehicleReq.getImage());
        subImageInfo.setImageID(sourceId);
        subImageInfo.setEventSort(2); //事件类型 过车:1
        subImageInfo.setShotTime(time);
        subImageInfo.setFileFormat("Jpeg");
        subImageInfo.setType("02"); // 图像类型 车牌彩色小图：02
        subImageInfo.setWidth(motorVehicleReq.getWidth());
        subImageInfo.setHeight(motorVehicleReq.getHeight());

        List<SubImageInfo> subImageInfoList = new ArrayList<>();
        subImageInfoList.add(subImageInfo);
        subImageList.setSubImageInfoObject(subImageInfoList);
        motorVehicle.setSubImageList(subImageList);
        motorVehicleList.add(motorVehicle);
        motorVehicleListObject.setMotorVehicleObject(motorVehicleList);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json;charset=utf-8"));
        headers.set("User-Identify", motorVehicleReq.getDeviceId());
        HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(motorVehicleRequestObject), headers);

        log.info("车牌上传消息体：{}", JSONUtil.toJsonStr(motorVehicleRequestObject));

       String url = "http://" + ip + ":" + port + "/VIID/MotorVehicles";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        int statusCode = responseEntity.getStatusCode().value();
        if (statusCode == HttpStatus.SC_OK) {
            String responseBody = responseEntity.getBody();
            if (StrUtil.isNotBlank(responseBody)) {
                ResponseStatusObjectWrapper responseStatusObjectWrapper = JSONUtil.toBean(responseBody, ResponseStatusObjectWrapper.class);
                int uploadStatus = responseStatusObjectWrapper.getResponseStatusObject().getStatusCode();
                if (uploadStatus == 0) {
                    return Result.success();
                }
            }
        }
        return Result.error("车牌信息上传失败："+responseEntity.getBody());
    }
}
