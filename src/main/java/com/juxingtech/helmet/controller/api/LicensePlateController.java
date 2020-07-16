package com.juxingtech.helmet.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.juxingtech.helmet.bean.LicensePlateReq;
import com.juxingtech.helmet.bean.LicensePlateResp;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.VehicleAll;
import com.juxingtech.helmet.service.IVehicleAllService;
import com.juxingtech.helmet.service.oss.MinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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

    public static Map<String, String> CSYS_MAP;
    public static Map<String, String> CLZT_MAP;


    public LicensePlateController() {
        CSYS_MAP = new HashMap<>();
        CLZT_MAP = new HashMap<>();
        // 车身颜色
        CSYS_MAP.put("A", "白");
        CSYS_MAP.put("B", "灰");
        CSYS_MAP.put("C", "黄");
        CSYS_MAP.put("D", "粉");
        CSYS_MAP.put("E", "红");
        CSYS_MAP.put("F", "紫");
        CSYS_MAP.put("G", "绿");
        CSYS_MAP.put("H", "蓝");
        CSYS_MAP.put("I", "棕");
        CSYS_MAP.put("J", "黑");

        // 车辆状态
        CLZT_MAP.put("A", "正常");
        CLZT_MAP.put("B", "转出");
        CLZT_MAP.put("C", "被盗抢");
        CLZT_MAP.put("D", "停驶");
        CLZT_MAP.put("E", "注销");
        CLZT_MAP.put("G", "违法未处理");
        CLZT_MAP.put("H", "海关监管");
        CLZT_MAP.put("I", "事故未处理");
        CLZT_MAP.put("J", "嫌疑车");
        CLZT_MAP.put("K", "查封");
        CLZT_MAP.put("L", "扣留");
        CLZT_MAP.put("M", "达到报废标准");
        CLZT_MAP.put("N", "事故逃逸");
        CLZT_MAP.put("O", "锁定");
        CLZT_MAP.put("P", "达到报废标准公告牌证作废");
        CLZT_MAP.put("Q", "逾期未检验");

    }

    @Autowired
    private IVehicleAllService iVehicleAllService;


    @Autowired
    private MinioService minioService;

    @PostMapping
    @ApiOperation(value = "车牌信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "req", value = "实体JSON对象", required = true, paramType = "body", dataType = "LicensePlateReq")
    })
    public Result upload(@RequestBody LicensePlateReq req) {
        LicensePlateResp resp = new LicensePlateResp();

        // minioService.uploadBase64(req.getImage());


        String plateNo = req.getPlateNo();

        String plateNoPrefix = plateNo.substring(0, 1);
        String plateNoSuffix = plateNo.substring(1);
        resp.setHpys(req.getPlateColor());
        resp.setHphm(req.getPlateNo());
        if (!plateNoPrefix.equals("冀")) {
            resp.setClzt("外地车牌");
            resp.setClpp("无");
            resp.setCsys("无");
            return Result.success(resp);
        }

        VehicleAll vehicleAll = iVehicleAllService.getOne(
                new LambdaQueryWrapper<VehicleAll>()
                        .eq(VehicleAll::getHphm, plateNoSuffix)
                        .eq(VehicleAll::getCllx, "K33")
        );

        String zt = vehicleAll.getZt();
        char[] chars = zt.toCharArray();
        String clzt = Strings.EMPTY;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String clztCode = String.valueOf(c);
            clzt += CLZT_MAP.get(clztCode) + ",";
        }
        resp.setClzt(clzt.substring(0, clzt.length() - 1));
        String csysCode = vehicleAll.getCsys();
        String csys = CSYS_MAP.get(csysCode);
        resp.setCsys(csys);
        resp.setClpp(vehicleAll.getClpp1());
        if (clzt.contains("正常")) {
            resp.setType(0);
        } else {
            resp.setType(1);
        }
        return Result.success(resp);
    }
}
