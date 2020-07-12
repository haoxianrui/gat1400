package com.juxingtech.helmet.controller.api;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.juxingtech.helmet.bean.HelmetInfo;
import com.juxingtech.helmet.common.constant.HelmetConstants;
import com.juxingtech.helmet.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @PostMapping
    @ApiOperation(value = "头盔（电量、位置）信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "helmetInfo", value = "实体JSON对象", required = true, paramType = "body", dataType = "HelmetInfo")
    })

    public Result upload(
            @RequestBody HelmetInfo helmetInfo
    ) {
        Assert.notBlank(helmetInfo.getSerialNo(), "头盔序列号不能为空");
        String helmetInfoJsonStr = JSONUtil.toJsonStr(helmetInfo);
        log.info(helmetInfoJsonStr);
        redisTemplate.opsForValue().set(HelmetConstants.REDIS_KEY_PREFIX_HELMET + helmetInfo.getSerialNo(), helmetInfoJsonStr, 300, TimeUnit.SECONDS);
        return Result.success();
    }
}
