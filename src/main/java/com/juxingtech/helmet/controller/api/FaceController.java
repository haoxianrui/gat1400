package com.juxingtech.helmet.controller.api;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.juxingtech.helmet.bean.FaceInfoReq;
import com.juxingtech.helmet.bean.FaceRequestObject;
import com.juxingtech.helmet.bean.ResponseStatusObjectWrapper;
import com.juxingtech.helmet.bean.SubImageInfo;
import com.juxingtech.helmet.common.constant.HelmetConstants;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.common.result.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
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

@Api(tags = "GAT/1400 人脸接口")
@RestController
@RequestMapping("/api/v1/faces")
@Slf4j
public class FaceController {


    @Value(value = "${push-server.ip}")
    private String ip;

    @Value(value = "${push-server.port}")
    private Integer port;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation(value = "人脸识别信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faceInfoReq", value = "实体JSON对象", required = true, paramType = "body", dataType = "FaceInfoReq")
    })
    public Result upload(
            @RequestBody FaceInfoReq faceInfoReq
    ) {
        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        FaceRequestObject faceRequestObject = new FaceRequestObject();
        FaceRequestObject.FaceListObject faceListObject = new FaceRequestObject.FaceListObject();
        faceRequestObject.setFaceListObject(faceListObject);

        List<FaceRequestObject.Face> faceList = new ArrayList<>();

        FaceRequestObject.Face face = new FaceRequestObject.Face();
        face.setInfoKind(0); // 信息分类 0:其他 1:自动采集 2:人工采集
        face.setLeftTopX(faceInfoReq.getLeftTopX());
        face.setLeftTopY(faceInfoReq.getLeftTopY());
        face.setRightBtmX(faceInfoReq.getRightBtmX());
        face.setRightBtmY(faceInfoReq.getRightBtmY());
        face.setIsForeigner(0); // 是否涉外人员
        face.setIsSuspectedTerrorist(0);// 是否涉恐人员
        face.setIsCriminalInvolved(0); // 是否涉案人员
        face.setIsDetainees(0); // 是否在押人员
        face.setIsVictim(0); //是否被害人
        face.setIsSuspiciousPerson(0); // 是否可疑人

        // 图像信息基本要素ID
        String sourceIdPrefix = faceInfoReq.getDeviceId() + "02" + time;
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
        face.setSourceID(sourceId);  // 图像基本要素ID String(41)

        // 图像信息内容要素ID
        String faceIdPrefix = sourceId + "06"; // 子类型编码 06-人脸
        String faceIdOrderNumKey = HelmetConstants.REDIS_KEY_PREFIX_ORDER_NUM + faceIdPrefix;
        Object faceIdOrderNumVal = redisTemplate.opsForValue().get(faceIdOrderNumKey);
        int faceIdOrderNum = 0;
        if (faceIdOrderNumVal != null) {
            faceIdOrderNum = Integer.valueOf(faceIdOrderNumVal.toString());
        }
        faceIdOrderNum += 1;
        redisTemplate.opsForValue().set(faceIdOrderNumKey, faceIdOrderNum, 1, TimeUnit.SECONDS);
        String faceIdOrderNumFormat = String.format("%05d", faceIdOrderNum);
        String faceId = faceIdPrefix + faceIdOrderNumFormat;
        face.setFaceID(faceId); // 人脸ID String(48)

        face.setDeviceID(faceInfoReq.getDeviceId());// 设备ID

        // 图片子对象信息
        FaceRequestObject.SubImageList subImageList = new FaceRequestObject.SubImageList();
        SubImageInfo subImageInfo = new SubImageInfo();
        subImageInfo.setDeviceID(faceInfoReq.getDeviceId());
        subImageInfo.setData(faceInfoReq.getImage());
        subImageInfo.setImageID(sourceId);
        subImageInfo.setEventSort(2); //事件类型 过人:2
        subImageInfo.setShotTime(time);
        subImageInfo.setFileFormat("Jpeg");
        subImageInfo.setType("11"); // 图像类型 人脸图：11
        subImageInfo.setWidth(faceInfoReq.getWidth());
        subImageInfo.setHeight(faceInfoReq.getHeight());

        List<SubImageInfo> subImageInfoList = new ArrayList<>();
        subImageInfoList.add(subImageInfo);
        subImageList.setSubImageInfoObject(subImageInfoList);
        face.setSubImageList(subImageList);
        faceList.add(face);
        faceListObject.setFaceObject(faceList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json;charset=utf-8"));
        headers.set("User-Identify", faceInfoReq.getDeviceId());
        HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(faceRequestObject), headers);
        log.info("上传人脸消息体：{}",JSONUtil.toJsonStr(faceRequestObject));

        String url = "http://" + ip + ":" + port + "/VIID/Faces";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        log.info("上传人脸响应结果：{}",responseEntity);
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
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            return Result.custom(ResultCodeEnum.CLIENT_REGISTER_ERROR);
        }
        return Result.error("人脸信息上传失败:" + responseEntity.getBody());
    }
}
