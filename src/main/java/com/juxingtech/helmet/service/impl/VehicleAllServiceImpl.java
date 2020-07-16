package com.juxingtech.helmet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juxingtech.helmet.entity.HmsHelmet;
import com.juxingtech.helmet.entity.VehicleAll;
import com.juxingtech.helmet.mapper.mysql.HmsHelmetMapper;
import com.juxingtech.helmet.mapper.oracle.VehicleAllMapper;
import com.juxingtech.helmet.service.IHmsHelmetService;
import com.juxingtech.helmet.service.IVehicleAllService;
import org.springframework.stereotype.Service;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Service
public class VehicleAllServiceImpl extends ServiceImpl<VehicleAllMapper,
        VehicleAll> implements IVehicleAllService {


}
