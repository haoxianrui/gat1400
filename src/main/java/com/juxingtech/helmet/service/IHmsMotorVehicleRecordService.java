package com.juxingtech.helmet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juxingtech.helmet.entity.HmsMotorVehicleRecord;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
public interface IHmsMotorVehicleRecordService extends IService<HmsMotorVehicleRecord> {
    IPage<HmsMotorVehicleRecord> list(HmsMotorVehicleRecord hmsMotorVehicleRecord, Page<HmsMotorVehicleRecord> page);
}
