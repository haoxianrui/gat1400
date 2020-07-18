package com.juxingtech.helmet.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juxingtech.helmet.common.result.PageResult;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsFaceRecord;
import com.juxingtech.helmet.service.IHmsFaceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Api
@RestController
@Slf4j
@RequestMapping("/face-records")
public class HmsFaceRecordController {
    @Resource
    private IHmsFaceRecordService iHmsFaceRecordService;

    @ApiOperation(value = "列表分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页数量", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "helmetName", value = "头盔名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "serialNo", value = "头盔序列号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", paramType = "query", dataType = "String")
    })
    @GetMapping
    public Result list(Integer page, Integer limit, String helmetName, String serialNo, String startDate, String endDate) {
        HmsFaceRecord hmsFaceRecord = new HmsFaceRecord();
        hmsFaceRecord.setHelmetName(helmetName);
        hmsFaceRecord.setSerialNo(serialNo);
        hmsFaceRecord.setStartDate(startDate);
        hmsFaceRecord.setEndDate(endDate);
        IPage<HmsFaceRecord> result = iHmsFaceRecordService.list(hmsFaceRecord, new Page<>(page, limit));
        return PageResult.success(result.getRecords(), result.getTotal());
    }

    @ApiOperation(value = "获取人脸识别记录数", httpMethod = "GET")
    @GetMapping("/count")
    public Result count() {
        int count = iHmsFaceRecordService.count();
        return Result.success(count);
    }
}
