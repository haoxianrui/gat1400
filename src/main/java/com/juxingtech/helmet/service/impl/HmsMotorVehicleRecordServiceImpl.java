package com.juxingtech.helmet.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juxingtech.helmet.entity.HmsMotorVehicleRecord;
import com.juxingtech.helmet.mapper.HmsMotorVehicleRecordMapper;
import com.juxingtech.helmet.service.IHmsMotorVehicleRecordService;
import org.springframework.stereotype.Service;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Service
public class HmsMotorVehicleRecordServiceImpl extends ServiceImpl<HmsMotorVehicleRecordMapper,
        HmsMotorVehicleRecord> implements IHmsMotorVehicleRecordService {


    @Override
    public IPage<HmsMotorVehicleRecord> list(HmsMotorVehicleRecord hmsMotorVehicleRecord, Page<HmsMotorVehicleRecord> page) {
        Page<HmsMotorVehicleRecord> result = this.baseMapper.page(hmsMotorVehicleRecord, page);
        return result;
    }
}
