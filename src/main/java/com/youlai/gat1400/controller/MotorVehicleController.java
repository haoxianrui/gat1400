package com.youlai.gat1400.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.youlai.gat1400.domain.ResponseStatusObjectWrapper;
import com.youlai.gat1400.common.enums.PlateColorEnum;
import com.youlai.gat1400.common.result.Result;
import com.youlai.gat1400.domain.MotorVehicleReq;
import com.youlai.gat1400.domain.MotorVehicleRequestObject;
import com.youlai.gat1400.domain.SubImageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Api(tags = "GAT/1400 机动车接口")
@RestController
@RequestMapping("/motor-vehicles")
@Slf4j
public class MotorVehicleController {


    @Autowired
    private RestTemplate restTemplate;

    String url;

    public MotorVehicleController(
            @Value("${server1400.ip}") String ip,
            @Value("${server1400.port}") String port
    ) {
        url = "http://" + ip + ":" + port + "/VIID/MotorVehicles";
    }


    @PostMapping
    @ApiOperation(value = "车牌信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "motorVehicleReq", value = "实体JSON对象", required = true, paramType = "body", dataType = "MotorVehicleReq")

    })
    public Result upload(
            @RequestBody MotorVehicleReq motorVehicleReq
    ) {
        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

        MotorVehicleRequestObject motorVehicleRequestObject = new MotorVehicleRequestObject();
        MotorVehicleRequestObject.MotorVehicleListObject motorVehicleListObject = new MotorVehicleRequestObject.MotorVehicleListObject();
        motorVehicleRequestObject.setMotorVehicleListObject(motorVehicleListObject);

        List<MotorVehicleRequestObject.MotorVehicle> motorVehicleList = new ArrayList<>();

        MotorVehicleRequestObject.MotorVehicle motorVehicle = new MotorVehicleRequestObject.MotorVehicle();
        motorVehicle.setInfoKind(1);  // 信息分类 0:其他 1:自动采集 2:人工采集

        // 图像信息基本要素ID
        String sourceId = motorVehicleReq.getDeviceId() + "02" + time + "00001";// 02:图像
        motorVehicle.setSourceID(sourceId);  // 图像基本要素ID String(41)
        motorVehicle.setLeftTopX(0);
        motorVehicle.setLeftTopY(0);
        motorVehicle.setRightBtmX(0);
        motorVehicle.setRightBtmY(0);

        motorVehicle.setHasPlate(true);
        motorVehicle.setPlateClass("99"); // 号牌种类 99-其他号牌
        motorVehicle.setPlateColor(PlateColorEnum.getCodeByName(motorVehicleReq.getPlateColor())); // 车牌颜色
        motorVehicle.setPlateNo(motorVehicleReq.getPlateNo());
        motorVehicle.setVehicleColor("99"); // 车身颜色 99-其他

        // 图像信息内容要素ID
        String motorVehicleId = sourceId + "02" + "00001"; // 子类型编码 02：机动车
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
        subImageInfo.setWidth(0);
        subImageInfo.setHeight(0);

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
        return Result.failed("车牌信息上传失败：" + responseEntity.getBody());
    }
}
