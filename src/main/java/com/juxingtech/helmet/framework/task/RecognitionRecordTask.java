package com.juxingtech.helmet.framework.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.juxingtech.helmet.entity.HmsFaceRecord;
import com.juxingtech.helmet.entity.HmsMotorVehicleRecord;
import com.juxingtech.helmet.entity.HmsRecognitionRecordStats;
import com.juxingtech.helmet.service.IHmsFaceRecordService;
import com.juxingtech.helmet.service.IHmsMotorVehicleRecordService;
import com.juxingtech.helmet.service.IHmsRecognitionRecordStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 识别记录定时任务
 */
@Component
@Slf4j
public class RecognitionRecordTask {

    @Autowired
    private IHmsRecognitionRecordStatsService iHmsRecognitionRecordStatsService;

    @Autowired
    private IHmsFaceRecordService iHmsFaceRecordService;

    @Autowired
    private IHmsMotorVehicleRecordService iHmsMotorVehicleRecordService;

    // 定时清理7天前的数据
    @Scheduled(cron = "0 0 0 * * ?")
    public void clean() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String deadline = dateTimeFormatter.format(now.minusDays(7));

        iHmsRecognitionRecordStatsService.remove(new LambdaQueryWrapper<HmsRecognitionRecordStats>().apply(
                "date_format(date,'%Y-%m-%d') <= date_format('" + deadline + "','%Y-%m-%d')"
        ));
    }

    // 统计上一天的人脸、车牌识别数
    @Scheduled(cron = "0 0 0 * * ?")
    public void stats() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String yesterday = dateTimeFormatter.format(now.minusDays(1));

        int faceRecordCount = iHmsFaceRecordService.count(new LambdaQueryWrapper<HmsFaceRecord>().apply(
                "date_format(alarm_time,'%Y-%m-%d') = date_format('" + yesterday + "','%Y-%m-%d')"
        ));

        int motorVehicleRecordCount = iHmsMotorVehicleRecordService.count(new LambdaQueryWrapper<HmsMotorVehicleRecord>().apply(
                "date_format(alarm_time,'%Y-%m-%d') = date_format('" + yesterday + "','%Y-%m-%d')"
        ));

        HmsRecognitionRecordStats hmsRecognitionRecordStats = new HmsRecognitionRecordStats();
        hmsRecognitionRecordStats.setDate(yesterday);
        hmsRecognitionRecordStats.setFaceRecordCount(faceRecordCount);
        hmsRecognitionRecordStats.setMotorVehicleRecordCount(motorVehicleRecordCount);
        iHmsRecognitionRecordStatsService.save(hmsRecognitionRecordStats);
    }
}
