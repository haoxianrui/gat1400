package com.juxingtech.helmet.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juxingtech.helmet.entity.HmsFaceRecord;
import com.juxingtech.helmet.mapper.HmsFaceRecordMapper;
import com.juxingtech.helmet.service.IHmsFaceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Service
@Slf4j
public class HmsFaceRecordServiceImpl extends ServiceImpl<HmsFaceRecordMapper, HmsFaceRecord> implements IHmsFaceRecordService {


    @Override
    public IPage<HmsFaceRecord> list(HmsFaceRecord hmsFaceRecord, Page<HmsFaceRecord> page) {
        Page<HmsFaceRecord> result = this.baseMapper.page(hmsFaceRecord, page);
        return result;
    }
}
