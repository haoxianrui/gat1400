package com.juxingtech.helmet.framework.task;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.juxingtech.helmet.entity.HmsFaceRecord;
import com.juxingtech.helmet.entity.HmsHelmet;
import com.juxingtech.helmet.entity.HmsMotorVehicleRecord;
import com.juxingtech.helmet.entity.HmsRecognitionRecordStats;
import com.juxingtech.helmet.service.IHmsFaceRecordService;
import com.juxingtech.helmet.service.IHmsHelmetService;
import com.juxingtech.helmet.service.IHmsMotorVehicleRecordService;
import com.juxingtech.helmet.service.IHmsRecognitionRecordStatsService;
import com.juxingtech.helmet.service.oss.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 识别记录定时任务
 */
@Component
@Slf4j
public class RecognitionRecordTask {

    @Autowired
    private IHmsHelmetService iHmsHelmetService;

    @Autowired
    private IHmsRecognitionRecordStatsService iHmsRecognitionRecordStatsService;

    @Autowired
    private IHmsFaceRecordService iHmsFaceRecordService;

    @Autowired
    private IHmsMotorVehicleRecordService iHmsMotorVehicleRecordService;

    @Autowired
    private MinioService minioService;

    // 定时清理7天前的数据
    @Scheduled(cron = "0 5 0 * * ?")
    public void clean() {
        log.info("删除数据定时任务开始");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String deadline = dateTimeFormatter.format(now.minusDays(7));

        iHmsRecognitionRecordStatsService.remove(new LambdaQueryWrapper<HmsRecognitionRecordStats>().apply(
                "date_format(date,'%Y-%m-%d') <= date_format('" + deadline + "','%Y-%m-%d')"
        ));

        // 清理人脸识别记录
        iHmsFaceRecordService.remove(new LambdaQueryWrapper<HmsFaceRecord>().apply(
                "date_format(create_time,'%Y-%m-%d') <= date_format('" + deadline + "','%Y-%m-%d')"
        ));


        // 清理车牌识别记录
        List<HmsMotorVehicleRecord> list = iHmsMotorVehicleRecordService.list(new LambdaQueryWrapper<HmsMotorVehicleRecord>().apply(
                "date_format(create_time,'%Y-%m-%d') <= date_format('" + deadline + "','%Y-%m-%d')"
        ));
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                minioService.delete(item.getImgUrl());
                iHmsMotorVehicleRecordService.removeById(item.getId());
            });
        }
    }

    // 统计上一天的人脸、车牌识别数
    @Scheduled(cron = "0 0 0 * * ?")
    public void stats() {
        log.info("统计定时任务开始");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String yesterday = dateTimeFormatter.format(now.minusDays(1));


        Map<String, Object> map = iHmsHelmetService.getMap(new QueryWrapper<HmsHelmet>()
                .eq("status", 1)
                .select("COALESCE(sum(face_count_today),0) as faceRecordCount",
                        "COALESCE(sum(vehicle_count_today),0) as motorVehicleRecordCount")
        );

        Long faceRecordCount = Long.valueOf(map.get("faceRecordCount").toString()) ;
        Long motorVehicleRecordCount = Long.valueOf(map.get("motorVehicleRecordCount").toString()) ;

        HmsRecognitionRecordStats hmsRecognitionRecordStats = new HmsRecognitionRecordStats();
        hmsRecognitionRecordStats.setDate(yesterday);
        hmsRecognitionRecordStats.setFaceRecordCount(faceRecordCount);
        hmsRecognitionRecordStats.setMotorVehicleRecordCount(motorVehicleRecordCount);
        iHmsRecognitionRecordStatsService.save(hmsRecognitionRecordStats);

        // 清零当天头盔统计数
        iHmsHelmetService.update(
                new LambdaUpdateWrapper<HmsHelmet>()
                        .set(HmsHelmet::getFaceCountToday, 0)
                        .set(HmsHelmet::getVehicleCountToday, 0));
    }
}
