package com.juxingtech.helmet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juxingtech.helmet.entity.HmsNetworkConfig;
import com.juxingtech.helmet.mapper.mysql.HmsNetworkConfigMapper;
import com.juxingtech.helmet.service.IHmsNetworkConfigService;
import org.springframework.stereotype.Service;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Service
public class HmsNetworkConfigServiceImpl extends ServiceImpl<HmsNetworkConfigMapper, HmsNetworkConfig> implements IHmsNetworkConfigService {
}
