package com.juxingtech.helmet.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.juxingtech.helmet.bean.LicensePlateReq;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsLicensePlate;
import com.juxingtech.helmet.service.IHmsLicensePlateService;
import com.juxingtech.helmet.service.oss.MinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "车牌接口")
@RestController
@RequestMapping("/api/v1/license-plates")
@Slf4j
public class LicensePlateController {

    @Autowired
    private IHmsLicensePlateService iHmsLicensePlateService;

    @Autowired
    private MinioService minioService;

    @PostMapping
    @ApiOperation(value = "车牌信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "licensePlateReq", value = "实体JSON对象", required = true, paramType = "body", dataType = "LicensePlateReq")
    })
    public Result upload(@RequestBody LicensePlateReq licensePlateReq) {

        minioService.uploadBase64(licensePlateReq.getImage());

        HmsLicensePlate licensePlate = iHmsLicensePlateService.getOne(new LambdaQueryWrapper<HmsLicensePlate>()
                .eq(HmsLicensePlate::getPlateNumber, licensePlateReq.getPlateNo())
                .eq(HmsLicensePlate::getPlateColor, licensePlateReq.getPlateColor()));

        Map<String, Object> map = new HashMap<>();
        if (licensePlate != null) {
            map.put("plateNumber", licensePlate.getPlateNumber());
            map.put("plateColor", licensePlate.getPlateColor());
            map.put("alarmContent", licensePlate.getAlarmContent());
            map.put("type", 1);
        } else {
            map.put("plateNumber", licensePlateReq.getPlateNo());
            map.put("plateColor", licensePlateReq.getPlateColor());
            map.put("type", 0);
        }
        return Result.success(map);
    }
}
