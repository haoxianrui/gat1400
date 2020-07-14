package com.juxingtech.helmet.controller.admin;

import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsNetworkConfig;
import com.juxingtech.helmet.service.IHmsNetworkConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@Slf4j
@RequestMapping("/network-configs")
public class HmsNetworkConfigController {

    @Autowired
    private IHmsNetworkConfigService iHmsNetworkConfigService;

    @ApiOperation(value = "视频网络配置详情", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "配置id", example = "1", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        HmsNetworkConfig networkConfig = iHmsNetworkConfigService.getById(id);
        if (networkConfig == null) {
            networkConfig = new HmsNetworkConfig();
        }
        return Result.success(networkConfig);
    }

    @ApiOperation(value = "视频网络配置保存", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hmsNetworkConfig", value = "实体JSON对象", required = true, paramType = "body", dataType = "HmsNetworkConfig")
    })
    @PutMapping
    public Result save(@RequestBody HmsNetworkConfig hmsNetworkConfig) {
        boolean status = iHmsNetworkConfigService.saveOrUpdate(hmsNetworkConfig);
        return Result.status(status);
    }
}
