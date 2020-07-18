package com.juxingtech.helmet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juxingtech.helmet.entity.HmsFaceRecord;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
public interface IHmsFaceRecordService extends IService<HmsFaceRecord> {
    IPage<HmsFaceRecord> list(HmsFaceRecord hmsFaceRecord, Page<HmsFaceRecord> page);
}
