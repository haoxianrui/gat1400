package com.juxingtech.helmet.controller.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juxingtech.helmet.bean.HelmetInfo;
import com.juxingtech.helmet.common.constant.HelmetConstants;
import com.juxingtech.helmet.common.result.PageResult;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsHelmet;
import com.juxingtech.helmet.service.IHmsHelmetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Api
@RestController
@Slf4j
@RequestMapping("/helmets")
public class HmsHelmetController {

    @Resource
    private IHmsHelmetService iHmsHelmetService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "列表分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "serialNo", value = "头盔序列号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "头盔名称", paramType = "query", dataType = "String"),
    })
    @GetMapping
    public Result list(Integer page, Integer limit, String serialNo, String name) {
        LambdaQueryWrapper<HmsHelmet> queryWrapper = new LambdaQueryWrapper<HmsHelmet>()
                .like(StrUtil.isNotBlank(serialNo), HmsHelmet::getSerialNo, serialNo)
                .like(StrUtil.isNotBlank(name), HmsHelmet::getName, name)
                .orderByDesc(HmsHelmet::getUpdateTime)
                .orderByDesc(HmsHelmet::getCreateTime);

        if (page != null && limit != null) {
            Page<HmsHelmet> result = iHmsHelmetService.page(new Page<>(page, limit), queryWrapper);
            if (result.getSize() > 0) {
                result.getRecords().forEach(item -> {
                    Object object = redisTemplate.opsForValue().get(HelmetConstants.REDIS_KEY_PREFIX_HELMET + item.getSerialNo());
                    if (object != null) {
                        String helmetInfoJsonStr = object.toString();
                        HelmetInfo helmetInfo = JSONUtil.toBean(helmetInfoJsonStr, HelmetInfo.class);
                        item.setStatus(1);
                        item.setElectricQuantity(helmetInfo.getElectricQuantity());
                    }
                });
            }
            return PageResult.success(result.getRecords(), result.getTotal());
        } else if (limit != null) {
            queryWrapper.last("LIMIT " + limit);
        }
        List<HmsHelmet> list = iHmsHelmetService.list(queryWrapper);
        return Result.success(list);
    }

    @ApiOperation(value = "头盔详情", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "头盔id", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        HmsHelmet hmsHelmet = iHmsHelmetService.getById(id);
        return Result.success(hmsHelmet);
    }

    @ApiOperation(value = "新增头盔", httpMethod = "POST")
    @ApiImplicitParam(name = "hmsHelmet", value = "实体JSON对象", required = true, paramType = "body", dataType = "HmsHelmet")
    @PostMapping
    public Result add(@RequestBody HmsHelmet hmsHelmet) {
        String serialNo = hmsHelmet.getSerialNo();
        int count = iHmsHelmetService.count(new LambdaQueryWrapper<HmsHelmet>().eq(HmsHelmet::getSerialNo, serialNo));
        Assert.isTrue(count <= 0, "头盔序列号已存在");
        hmsHelmet.setCreateTime(new Date());
        boolean status = iHmsHelmetService.save(hmsHelmet);
        return Result.status(status);
    }

    @ApiOperation(value = "修改头盔", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "头盔id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "hmsHelmet", value = "实体JSON对象", required = true, paramType = "body", dataType = "HmsHelmet")
    })
    @PutMapping(value = "/{id}")
    public Result update(
            @PathVariable Long id,
            @RequestBody HmsHelmet hmsHelmet) {
        hmsHelmet.setUpdateTime(new Date());
        boolean status = iHmsHelmetService.updateById(hmsHelmet);
        return Result.status(status);
    }

    @ApiOperation(value = "删除头盔", httpMethod = "DELETE")
    @ApiImplicitParam(name = "ids[]", value = "id集合", required = true, paramType = "query", allowMultiple = true, dataType = "Long")
    @DeleteMapping
    public Result delete(@RequestParam("ids") List<Long> ids) {
        boolean status = iHmsHelmetService.removeByIds(ids);
        return Result.status(status);
    }
}
