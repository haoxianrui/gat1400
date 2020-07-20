package com.juxingtech.helmet.controller.api;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.juxingtech.helmet.bean.HelmetInfo;
import com.juxingtech.helmet.common.constant.HelmetConstants;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsHelmet;
import com.juxingtech.helmet.entity.HmsNetworkConfig;
import com.juxingtech.helmet.service.IHmsHelmetService;
import com.juxingtech.helmet.service.IHmsNetworkConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @author haoxr
 * @date 2020-07-02
 **/
@Api(tags = "头盔接口")
@RestController
@RequestMapping("/api/v1/helmets")
@Slf4j
public class HelmetController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IHmsHelmetService iHmsHelmetService;

    @Autowired
    private IHmsNetworkConfigService iHmsNetworkConfigService;

    @PostMapping
    @ApiOperation(value = "头盔（电量、位置）信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "helmetInfo", value = "实体JSON对象", required = true, paramType = "body", dataType = "HelmetInfo")
    })

    public Result upload(
            @RequestBody HelmetInfo helmetInfo
    ) {
        log.info(helmetInfo.toString());
        Assert.notBlank(helmetInfo.getSerialNo(), "头盔序列号不能为空");
        String helmetInfoJsonStr = JSONUtil.toJsonStr(helmetInfo);
        log.info(helmetInfoJsonStr);
        redisTemplate.opsForValue().set(HelmetConstants.REDIS_KEY_PREFIX_HELMET + helmetInfo.getSerialNo(), helmetInfoJsonStr, 300, TimeUnit.SECONDS);
        return Result.success();
    }


    @GetMapping("/{serialNo}")
    @ApiOperation(value = "获取设备ID和配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serialNo", value = "头盔序列号", example = "697266759002320011800001", required = true, paramType = "path")
    })
    public Result config(
            @PathVariable String serialNo
    ) {
        HmsNetworkConfig networkConfig = iHmsNetworkConfigService.getById(1);
        Assert.notNull(networkConfig, "未查找到配置信息，请联系系统管理员");

        HmsHelmet hmsHelmet = iHmsHelmetService.getOne(new LambdaQueryWrapper<HmsHelmet>()
                .eq(HmsHelmet::getSerialNo, serialNo)
                .eq(HmsHelmet::getStatus, 1)
        );
        Assert.notNull(hmsHelmet, "头盔不存在或未启动，请联系系统管理员");

        networkConfig.setImgDeviceId(hmsHelmet.getImgDeviceId());
        networkConfig.setVideoDeviceId(hmsHelmet.getVideoDeviceId());
        return Result.success(networkConfig);
    }



}
