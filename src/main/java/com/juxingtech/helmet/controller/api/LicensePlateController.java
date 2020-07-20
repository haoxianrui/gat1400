package com.juxingtech.helmet.controller.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.juxingtech.helmet.bean.LicensePlateReq;
import com.juxingtech.helmet.bean.LicensePlateResp;
import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.entity.HmsMotorVehicleRecord;
import com.juxingtech.helmet.entity.VehicleAll;
import com.juxingtech.helmet.service.IHmsMotorVehicleRecordService;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "车牌接口")
@RestController
@RequestMapping("/api/v1/license-plates")
@Slf4j
public class LicensePlateController {

    @Autowired
    private IVehicleAllService iVehicleAllService;

    @Autowired
    private IHmsMotorVehicleRecordService iHmsMotorVehicleRecordService;

    @Autowired
    private MinioService minioService;

    @PostMapping
    @ApiOperation(value = "车牌信息上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "req", value = "实体JSON对象", required = true, paramType = "body", dataType = "LicensePlateReq")
    })
    public Result upload(@RequestBody LicensePlateReq req) {
        LicensePlateResp resp = new LicensePlateResp();
        // 只识别号牌种类 01-黄牌  02-蓝牌 的车牌
        String plateColor = req.getPlateColor();
        String hpzl;
        resp.setHphm(req.getPlateNo());
        resp.setHpys(req.getPlateColor());
        if (plateColor.equals("蓝")) {
            hpzl = "02";
        } else if (plateColor.equals("黄")) {
            hpzl = "01";
        } else {
            resp.setStatus(0);
            resp.setClzt("未识别");
            return Result.success(resp);
        }

        String plateNo = req.getPlateNo();
        String plateNoPrefix = plateNo.substring(0, 1);
        String plateNoSuffix = plateNo.substring(1);
        if (!plateNoPrefix.equals("冀")) {
            resp.setStatus(0);
            resp.setClzt("外地车牌");
            return Result.success(resp);
        }

        VehicleAll vehicleAll = iVehicleAllService.getOne(
                new LambdaQueryWrapper<VehicleAll>()
                        .eq(VehicleAll::getHphm, plateNoSuffix)
                        .eq(VehicleAll::getHpzl, hpzl)
        );
        if (vehicleAll == null) {
            resp.setStatus(0);
            resp.setClzt("未识别");
            return Result.success(resp);
        }
        HmsMotorVehicleRecord hmsMotorVehicleRecord = new HmsMotorVehicleRecord();
        String zt = vehicleAll.getZt();
        char[] chars = zt.toCharArray();
        String clzt = Strings.EMPTY;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String clztCode = String.valueOf(c);
            clzt += CLZT_MAP.get(clztCode) + ",";
        }
        if(StrUtil.isNotBlank(clzt)){
            clzt=clzt.substring(0, clzt.length() - 1);
        }
        resp.setClzt(clzt);
        String csysCode = vehicleAll.getCsys();
        String csys = CSYS_MAP.get(csysCode);
        resp.setCsys(csys);
        resp.setClpp(vehicleAll.getClpp1());
        if (clzt.contains("正常")) {
            resp.setType(0);
        } else {
            resp.setType(1);
        }
        String cllx = CLLX_MAP.get(vehicleAll.getCllx());
        String syxz = SYXZ_MAP.get(vehicleAll.getSyxz());
        resp.setCllx(cllx);
        resp.setSyxz(syxz);
        resp.setStatus(1);
        // 保存记录
        hmsMotorVehicleRecord.setDeviceId(req.getDeviceId());
        hmsMotorVehicleRecord.setAlarmTime(new Date());
        hmsMotorVehicleRecord.setAlarmContent(clzt);
        hmsMotorVehicleRecord.setType(resp.getType());
        hmsMotorVehicleRecord.setNumber(req.getPlateNo());
        hmsMotorVehicleRecord.setColor(req.getPlateColor());
        hmsMotorVehicleRecord.setCreateTime(new Date());
        String imgUrl = minioService.uploadBase64(req.getImage());
        hmsMotorVehicleRecord.setImgUrl(imgUrl);
        iHmsMotorVehicleRecordService.save(hmsMotorVehicleRecord);

        return Result.success(resp);
    }


    public static Map<String, String> CSYS_MAP;
    public static Map<String, String> CLZT_MAP;
    public static Map<String, String> SYXZ_MAP;
    public static Map<String, String> CLLX_MAP;

    public LicensePlateController() {
        CSYS_MAP = new HashMap<>();
        CLZT_MAP = new HashMap<>();
        SYXZ_MAP = new HashMap<>();
        CLLX_MAP = new HashMap<>();
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

        //使用性质

        SYXZ_MAP.put("A", "非营运");
        SYXZ_MAP.put("B", "公路客运");
        SYXZ_MAP.put("C", "公交客运");
        SYXZ_MAP.put("D", "出租客运");
        SYXZ_MAP.put("E", "旅游客运");
        SYXZ_MAP.put("F", "货运");
        SYXZ_MAP.put("G", "租赁");
        SYXZ_MAP.put("H", "警用");
        SYXZ_MAP.put("I", "消防");
        SYXZ_MAP.put("J", "救护");
        SYXZ_MAP.put("K", "工程救险");
        SYXZ_MAP.put("L", "营转非");
        SYXZ_MAP.put("M", "出租转非");
        SYXZ_MAP.put("N", "教练");
        SYXZ_MAP.put("O", "幼儿校车");
        SYXZ_MAP.put("P", "小学生校车");
        SYXZ_MAP.put("Q", "初中生校车");
        SYXZ_MAP.put("R", "危化品运输");
        SYXZ_MAP.put("S", "中小学生校车");
        SYXZ_MAP.put("T", "预约出租客运");
        SYXZ_MAP.put("U", "预约出租转非");

        // 车辆类型
        CLLX_MAP.put("B11", "重型普通半挂车");
        CLLX_MAP.put("B12", "重型厢式半挂车");
        CLLX_MAP.put("B13", "重型罐式半挂车");
        CLLX_MAP.put("B14", "重型平板半挂车");
        CLLX_MAP.put("B15", "重型集装箱半挂车");
        CLLX_MAP.put("B16", "重型自卸半挂车");
        CLLX_MAP.put("B17", "重型特殊结构半挂车");
        CLLX_MAP.put("B18", "重型仓栅式半挂车");
        CLLX_MAP.put("B19", "重型旅居半挂车");
        CLLX_MAP.put("B1A", "重型专项作业半挂车");
        CLLX_MAP.put("B1B", "重型低平板半挂车");
        CLLX_MAP.put("B1C", "重型车辆运输半挂车");
        CLLX_MAP.put("B1D", "重型罐式自卸半挂车");
        CLLX_MAP.put("B1E", "重型平板自卸半挂车");
        CLLX_MAP.put("B1F", "重型集装箱自卸半挂车");
        CLLX_MAP.put("B1G", "重型特殊结构自卸半挂车");
        CLLX_MAP.put("B1H", "重型仓栅式自卸半挂车");
        CLLX_MAP.put("B1J", "重型专项作业自卸半挂车");
        CLLX_MAP.put("B1K", "重型低平板自卸半挂车");
        CLLX_MAP.put("B1U", "重型中置轴旅居挂车");
        CLLX_MAP.put("B1V", "重型中置轴车辆运输车");
        CLLX_MAP.put("B1W", "重型中置轴普通挂车");
        CLLX_MAP.put("B21", "中型普通半挂车");
        CLLX_MAP.put("B22", "中型厢式半挂车");
        CLLX_MAP.put("B23", "中型罐式半挂车");
        CLLX_MAP.put("B24", "中型平板半挂车");
        CLLX_MAP.put("B25", "中型集装箱半挂车");
        CLLX_MAP.put("B26", "中型自卸半挂车");
        CLLX_MAP.put("B27", "中型特殊结构半挂车");
        CLLX_MAP.put("B28", "中型仓栅式半挂车");
        CLLX_MAP.put("B29", "中型旅居半挂车");
        CLLX_MAP.put("B2A", "中型专项作业半挂车");
        CLLX_MAP.put("B2B", "中型低平板半挂车");
        CLLX_MAP.put("B2C", "中型车辆运输半挂车");
        CLLX_MAP.put("B2D", "中型罐式自卸半挂车");
        CLLX_MAP.put("B2E", "中型平板自卸半挂车");
        CLLX_MAP.put("B2F", "中型集装箱自卸半挂车");
        CLLX_MAP.put("B2G", "中型特殊结构自卸半挂车");
        CLLX_MAP.put("B2H", "中型仓栅式自卸半挂车");
        CLLX_MAP.put("B2J", "中型专项作业自卸半挂车");
        CLLX_MAP.put("B2K", "中型低平板自卸半挂车");
        CLLX_MAP.put("B2U", "中型中置轴旅居挂车");
        CLLX_MAP.put("B2V", "中型中置轴车辆运输车");
        CLLX_MAP.put("B2W", "中型中置轴普通挂车");
        CLLX_MAP.put("B31", "轻型普通半挂车");
        CLLX_MAP.put("B32", "轻型厢式半挂车");
        CLLX_MAP.put("B33", "轻型罐式半挂车");
        CLLX_MAP.put("B34", "轻型平板半挂车");
        CLLX_MAP.put("B35", "轻型自卸半挂车");
        CLLX_MAP.put("B36", "轻型仓栅式半挂车");
        CLLX_MAP.put("B37", "轻型旅居半挂车");
        CLLX_MAP.put("B38", "轻型专项作业半挂车");
        CLLX_MAP.put("B39", "轻型低平板半挂车");
        CLLX_MAP.put("B3C", "轻型车辆运输半挂车");
        CLLX_MAP.put("B3D", "轻型罐式自卸半挂车");
        CLLX_MAP.put("B3E", "轻型平板自卸半挂车");
        CLLX_MAP.put("B3F", "轻型集装箱自卸半挂车");
        CLLX_MAP.put("B3G", "轻型特殊结构自卸半挂车");
        CLLX_MAP.put("B3H", "轻型仓栅式自卸半挂车");
        CLLX_MAP.put("B3J", "轻型专项作业自卸半挂车");
        CLLX_MAP.put("B3K", "轻型低平板自卸半挂车");
        CLLX_MAP.put("B3U", "轻型中置轴旅居挂车");
        CLLX_MAP.put("B3V", "轻型中置轴车辆运输车");
        CLLX_MAP.put("B3W", "轻型中置轴普通挂车");
        CLLX_MAP.put("D11", "无轨电车");
        CLLX_MAP.put("D12", "有轨电车");
        CLLX_MAP.put("G11", "重型普通全挂车");
        CLLX_MAP.put("G12", "重型厢式全挂车");
        CLLX_MAP.put("G13", "重型罐式全挂车");
        CLLX_MAP.put("G14", "重型平板全挂车");
        CLLX_MAP.put("G15", "重型集装箱全挂车");
        CLLX_MAP.put("G16", "重型自卸全挂车");
        CLLX_MAP.put("G17", "重型仓栅式全挂车");
        CLLX_MAP.put("G18", "重型旅居全挂车");
        CLLX_MAP.put("G19", "重型专项作业全挂车");
        CLLX_MAP.put("G1A", "重型厢式自卸全挂车");
        CLLX_MAP.put("G1B", "重型罐式自卸全挂车");
        CLLX_MAP.put("G1C", "重型平板自卸全挂车");
        CLLX_MAP.put("G1D", "重型集装箱自卸全挂车");
        CLLX_MAP.put("G1E", "重型仓栅式自卸全挂车");
        CLLX_MAP.put("G1F", "重型专项作业自卸全挂车");
        CLLX_MAP.put("G21", "中型普通全挂车");
        CLLX_MAP.put("G22", "中型厢式全挂车");
        CLLX_MAP.put("G23", "中型罐式全挂车");
        CLLX_MAP.put("G24", "中型平板全挂车");
        CLLX_MAP.put("G25", "中型集装箱全挂车");
        CLLX_MAP.put("G26", "中型自卸全挂车");
        CLLX_MAP.put("G27", "中型仓栅式全挂车");
        CLLX_MAP.put("G28", "中型旅居全挂车");
        CLLX_MAP.put("G29", "中型专项作业全挂车");
        CLLX_MAP.put("G2A", "中型厢式自卸全挂车");
        CLLX_MAP.put("G2B", "中型罐式自卸全挂车");
        CLLX_MAP.put("G2C", "中型平板自卸全挂车");
        CLLX_MAP.put("G2D", "中型集装箱自卸全挂车");
        CLLX_MAP.put("G2E", "中型仓栅式自卸全挂车");
        CLLX_MAP.put("G2F", "中型专项作业自卸全挂车");
        CLLX_MAP.put("G31", "轻型普通全挂车");
        CLLX_MAP.put("G32", "轻型厢式全挂车");
        CLLX_MAP.put("G33", "轻型罐式全挂车");
        CLLX_MAP.put("G34", "轻型平板全挂车");
        CLLX_MAP.put("G35", "轻型自卸全挂车");
        CLLX_MAP.put("G36", "轻型仓栅式全挂车");
        CLLX_MAP.put("G37", "轻型旅居全挂车");
        CLLX_MAP.put("G38", "轻型专项作业全挂车");
        CLLX_MAP.put("G3A", "轻型厢式自卸全挂车");
        CLLX_MAP.put("G3B", "轻型罐式自卸全挂车");
        CLLX_MAP.put("G3C", "轻型平板自卸全挂车");
        CLLX_MAP.put("G3D", "轻型集装箱自卸全挂车");
        CLLX_MAP.put("G3E", "轻型仓栅式自卸全挂车");
        CLLX_MAP.put("G3F", "轻型专项作业自卸全挂车");
        CLLX_MAP.put("H11", "重型普通货车");
        CLLX_MAP.put("H12", "重型厢式货车");
        CLLX_MAP.put("H13", "重型封闭货车");
        CLLX_MAP.put("H14", "重型罐式货车");
        CLLX_MAP.put("H15", "重型平板货车");
        CLLX_MAP.put("H16", "重型集装厢车");
        CLLX_MAP.put("H17", "重型自卸货车");
        CLLX_MAP.put("H18", "重型特殊结构货车");
        CLLX_MAP.put("H19", "重型仓栅式货车");
        CLLX_MAP.put("H1A", "重型车辆运输车");
        CLLX_MAP.put("H1B", "重型厢式自卸货车");
        CLLX_MAP.put("H1C", "重型罐式自卸货车");
        CLLX_MAP.put("H1D", "重型平板自卸货车");
        CLLX_MAP.put("H1E", "重型集装厢自卸货车");
        CLLX_MAP.put("H1F", "重型特殊结构自卸货车");
        CLLX_MAP.put("H1G", "重型仓栅式自卸货车");
        CLLX_MAP.put("H21", "中型普通货车");
        CLLX_MAP.put("H22", "中型厢式货车");
        CLLX_MAP.put("H23", "中型封闭货车");
        CLLX_MAP.put("H24", "中型罐式货车");
        CLLX_MAP.put("H25", "中型平板货车");
        CLLX_MAP.put("H26", "中型集装厢车");
        CLLX_MAP.put("H27", "中型自卸货车");
        CLLX_MAP.put("H28", "中型特殊结构货车");
        CLLX_MAP.put("H29", "中型仓栅式货车");
        CLLX_MAP.put("H2A", "中型车辆运输车");
        CLLX_MAP.put("H2B", "中型厢式自卸货车");
        CLLX_MAP.put("H2C", "中型罐式自卸货车");
        CLLX_MAP.put("H2D", "中型平板自卸货车");
        CLLX_MAP.put("H2E", "中型集装厢自卸货车");
        CLLX_MAP.put("H2F", "中型特殊结构自卸货车");
        CLLX_MAP.put("H2G", "中型仓栅式自卸货车");
        CLLX_MAP.put("H31", "轻型普通货车");
        CLLX_MAP.put("H32", "轻型厢式货车");
        CLLX_MAP.put("H33", "轻型封闭货车");
        CLLX_MAP.put("H34", "轻型罐式货车");
        CLLX_MAP.put("H35", "轻型平板货车");
        CLLX_MAP.put("H37", "轻型自卸货车");
        CLLX_MAP.put("H38", "轻型特殊结构货车");
        CLLX_MAP.put("H39", "轻型仓栅式货车");
        CLLX_MAP.put("H3A", "轻型车辆运输车");
        CLLX_MAP.put("H3B", "轻型厢式自卸货车");
        CLLX_MAP.put("H3C", "轻型罐式自卸货车");
        CLLX_MAP.put("H3D", "轻型平板自卸货车");
        CLLX_MAP.put("H3F", "轻型特殊结构自卸货车");
        CLLX_MAP.put("H3G", "轻型仓栅式自卸货车");
        CLLX_MAP.put("H41", "微型普通货车");
        CLLX_MAP.put("H42", "微型厢式货车");
        CLLX_MAP.put("H43", "微型封闭货车");
        CLLX_MAP.put("H44", "微型罐式货车");
        CLLX_MAP.put("H45", "微型自卸货车");
        CLLX_MAP.put("H46", "微型特殊结构货车");
        CLLX_MAP.put("H47", "微型仓栅式货车");
        CLLX_MAP.put("H4A", "微型车辆运输车");
        CLLX_MAP.put("H4B", "微型厢式自卸货车");
        CLLX_MAP.put("H4C", "微型罐式自卸货车");
        CLLX_MAP.put("H4F", "微型特殊结构自卸货车");
        CLLX_MAP.put("H4G", "微型仓栅式自卸货车");
        CLLX_MAP.put("H51", "普通低速货车");
        CLLX_MAP.put("H52", "厢式低速货车");
        CLLX_MAP.put("H53", "罐式低速货车");
        CLLX_MAP.put("H54", "自卸低速货车");
        CLLX_MAP.put("H55", "仓栅式低速货车");
        CLLX_MAP.put("H5B", "厢式自卸低速货车");
        CLLX_MAP.put("H5C", "罐式自卸低速货车");
        CLLX_MAP.put("J11", "轮式装载机械");
        CLLX_MAP.put("J12", "轮式挖掘机械");
        CLLX_MAP.put("J13", "轮式平地机械");
        CLLX_MAP.put("K11", "大型普通客车");
        CLLX_MAP.put("K12", "大型双层客车");
        CLLX_MAP.put("K13", "大型卧铺客车");
        CLLX_MAP.put("K14", "大型铰接客车");
        CLLX_MAP.put("K15", "大型越野客车");
        CLLX_MAP.put("K16", "大型轿车");
        CLLX_MAP.put("K17", "大型专用客车");
        CLLX_MAP.put("K18", "大型专用校车");
        CLLX_MAP.put("K21", "中型普通客车");
        CLLX_MAP.put("K22", "中型双层客车");
        CLLX_MAP.put("K23", "中型卧铺客车");
        CLLX_MAP.put("K24", "中型铰接客车");
        CLLX_MAP.put("K25", "中型越野客车");
        CLLX_MAP.put("K26", "中型轿车");
        CLLX_MAP.put("K27", "中型专用客车");
        CLLX_MAP.put("K28", "中型专用校车");
        CLLX_MAP.put("K31", "小型普通客车");
        CLLX_MAP.put("K32", "小型越野客车");
        CLLX_MAP.put("K33", "小型轿车");
        CLLX_MAP.put("K34", "小型专用客车");
        CLLX_MAP.put("K38", "小型专用校车");
        CLLX_MAP.put("K39", "小型面包车");
        CLLX_MAP.put("K41", "微型普通客车");
        CLLX_MAP.put("K42", "微型越野客车");
        CLLX_MAP.put("K43", "微型轿车");
        CLLX_MAP.put("K49", "微型面包车");
        CLLX_MAP.put("M11", "普通正三轮摩托车");
        CLLX_MAP.put("M12", "轻便正三轮摩托车");
        CLLX_MAP.put("M13", "正三轮载客摩托车");
        CLLX_MAP.put("M14", "正三轮载货摩托车");
        CLLX_MAP.put("M15", "侧三轮摩托车");
        CLLX_MAP.put("M21", "普通二轮摩托车");
        CLLX_MAP.put("M22", "轻便二轮摩托车");
        CLLX_MAP.put("N11", "三轮汽车");
        CLLX_MAP.put("Q11", "重型半挂牵引车");
        CLLX_MAP.put("Q12", "重型全挂牵引车");
        CLLX_MAP.put("Q21", "中型半挂牵引车");
        CLLX_MAP.put("Q22", "中型全挂牵引车");
        CLLX_MAP.put("Q31", "轻型半挂牵引车");
        CLLX_MAP.put("Q32", "轻型全挂牵引车");
        CLLX_MAP.put("T11", "大型轮式拖拉机");
        CLLX_MAP.put("T21", "小型轮式拖拉机");
        CLLX_MAP.put("T22", "手扶拖拉机");
        CLLX_MAP.put("T23", "手扶变形运输机");
        CLLX_MAP.put("X99", "其它");
        CLLX_MAP.put("Z11", "大型非载货专项作业车");
        CLLX_MAP.put("Z12", "大型载货专项作业车");
        CLLX_MAP.put("Z21", "中型非载货专项作业车");
        CLLX_MAP.put("Z22", "中型载货专项作业车");
        CLLX_MAP.put("Z31", "小型非载货专项作业车");
        CLLX_MAP.put("Z32", "小型载货专项作业车");
        CLLX_MAP.put("Z41", "微型非载货专项作业车");
        CLLX_MAP.put("Z42", "微型载货专项作业车");
        CLLX_MAP.put("Z51", "重型非载货专项作业车");
        CLLX_MAP.put("Z52", "重型载货专项作业车");
        CLLX_MAP.put("Z71", "轻型非载货专项作业车");
        CLLX_MAP.put("Z72", "轻型载货专项作业车");

    }
}
