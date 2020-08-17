package com.juxingtech.helmet.controller.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juxingtech.helmet.common.result.PageResult;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsLicensePlate;
import com.juxingtech.helmet.service.IHmsLicensePlateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Api
@RestController
@Slf4j
@RequestMapping("/license-plates")
public class HmsLicensePlateController {

    @Resource
    private IHmsLicensePlateService iHmsLicensePlateService;


    @ApiOperation(value = "列表分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "username", value = "姓名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "plateNo", value = "车牌号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "车牌类型", paramType = "query", dataType = "String"),
    })
    @GetMapping
    public Result list(Integer page, Integer limit, String username, String plateNo, Integer type) {
        LambdaQueryWrapper<HmsLicensePlate> queryWrapper = new LambdaQueryWrapper<HmsLicensePlate>()
                .like(StrUtil.isNotBlank(username), HmsLicensePlate::getUsername, username)
                .like(StrUtil.isNotBlank(plateNo), HmsLicensePlate::getPlateNo, plateNo)
                .eq(type != null, HmsLicensePlate::getType, type)
                .orderByDesc(HmsLicensePlate::getUpdateTime)
                .orderByDesc(HmsLicensePlate::getCreateTime);

        if (page != null && limit != null) {
            Page<HmsLicensePlate> result = iHmsLicensePlateService.page(new Page<>(page, limit), queryWrapper);
            return PageResult.success(result.getRecords(), result.getTotal());
        } else if (limit != null) {
            queryWrapper.last("LIMIT " + limit);
        }
        List<HmsLicensePlate> list = iHmsLicensePlateService.list(queryWrapper);
        return Result.success(list);
    }

    @ApiOperation(value = "车牌详情", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "头盔id", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        HmsLicensePlate HmsLicensePlate = iHmsLicensePlateService.getById(id);
        return Result.success(HmsLicensePlate);
    }

    @ApiOperation(value = "新增车牌", httpMethod = "POST")
    @ApiImplicitParam(name = "HmsLicensePlate", value = "实体JSON对象", required = true, paramType = "body", dataType = "HmsLicensePlate")
    @PostMapping
    public Result add(@RequestBody HmsLicensePlate hmsLicensePlate) {
        String plateNo = hmsLicensePlate.getPlateNo();
        int count = iHmsLicensePlateService.count(new LambdaQueryWrapper<HmsLicensePlate>().eq(HmsLicensePlate::getPlateNo,
                plateNo));
        Assert.isTrue(count <= 0, "车牌号已存在");
        Date date = new Date();
        hmsLicensePlate.setCreateTime(date);
        hmsLicensePlate.setUpdateTime(date);
        boolean status = iHmsLicensePlateService.save(hmsLicensePlate);
        return Result.status(status);
    }

    @ApiOperation(value = "修改车牌", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "头盔id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "HmsLicensePlate", value = "实体JSON对象", required = true, paramType = "body", dataType = "HmsLicensePlate")
    })
    @PutMapping(value = "/{id}")
    public Result update(
            @PathVariable Long id,
            @RequestBody HmsLicensePlate hmsLicensePlate) {
        hmsLicensePlate.setUpdateTime(new Date());
        boolean status = iHmsLicensePlateService.updateById(hmsLicensePlate);
        return Result.status(status);
    }

    @ApiOperation(value = "删除车牌", httpMethod = "DELETE")
    @DeleteMapping
    public Result delete(@RequestBody String[] ids) {
        List<String> idList = Arrays.asList(ids);
        boolean status = iHmsLicensePlateService.removeByIds(idList);
        return Result.status(status);
    }


    @PostMapping("/excel")
    public Result uploadExcel(@RequestParam(value = "file") MultipartFile file,@RequestParam("type") Integer type) {
        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return Result.error("上传文件格式不正确");
        }
        try {
            Date date = new Date();
            InputStream inputStream = file.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));
            HSSFSheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            List<HmsLicensePlate> list = new ArrayList<>();
            for (int i = 2; i < rows; i++) {
                HSSFRow row = sheet.getRow(i);
                int cells = row.getPhysicalNumberOfCells();
                String username = row.getCell(1).getStringCellValue();
                for (int j = 2; j < cells; j++) {
                    String plateNo = row.getCell(j).getStringCellValue();
                    if (StrUtil.isNotBlank(plateNo)) {
                        HmsLicensePlate plate = new HmsLicensePlate();
                        plate.setUsername(username);
                        plate.setPlateNo(plateNo);
                        list.add(plate);
                    } else {
                        break;
                    }
                }
            }

            int totalNum = list.size();
            int repeatNum = 0;
            List<HmsLicensePlate> addList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                HmsLicensePlate hmsLicensePlate = list.get(i);
                int count = iHmsLicensePlateService.count(new LambdaQueryWrapper<HmsLicensePlate>()
                        .eq(HmsLicensePlate::getPlateNo, hmsLicensePlate.getPlateNo()));
                if (count >= 1) {
                    repeatNum++;
                } else {
                    hmsLicensePlate.setType(type);
                    hmsLicensePlate.setCreateTime(date);
                    hmsLicensePlate.setUpdateTime(date);
                    addList.add(hmsLicensePlate);
                }
            }
            int successNum = addList.size();
            if (successNum > 0) {
                iHmsLicensePlateService.saveBatch(addList);
            }
            return Result.success("导入成功。车牌总条数:" + totalNum +
                    "，重复车牌数:" + repeatNum + "，导入成功数:" + successNum);

        } catch (Exception e) {
            return Result.error("导入失败");
        }

    }
}
