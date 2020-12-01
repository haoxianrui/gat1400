package com.juxingtech.helmet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juxingtech.helmet.entity.HmsFaceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Mapper
public interface HmsFaceRecordMapper extends BaseMapper<HmsFaceRecord> {

    @Select("<script>" +
            " SELECT " +
            " a.*, b.name helmet_name,b.serial_no " +
            " FROM hms_face_record a " +
            " LEFT JOIN hms_helmet b ON a.device_id = b.img_device_id " +
            " where 1=1 " +
            "<if test='hmsFaceRecord.helmetName!=null and hmsFaceRecord.helmetName.trim() neq \"\"'>" +
            "   AND b.name like concat('%',#{hmsFaceRecord.helmetName},'%') " +
            "</if>"+
            "<if test='hmsFaceRecord.serialNo!=null and hmsFaceRecord.serialNo.trim() neq \"\"'>" +
            "   AND b.serial_no like concat('%',#{hmsFaceRecord.serialNo},'%') " +
            "</if>"+
            "<if test='hmsFaceRecord.startDate!=null and hmsFaceRecord.startDate.trim() neq \"\"'>" +
            "   AND date_format (a.alarm_time,'%Y-%m-%d') &gt;=date_format(#{hmsFaceRecord.startDate},'%Y-%m-%d') " +
            "</if>"+
            "<if test='hmsFaceRecord.endDate!=null and hmsFaceRecord.endDate.trim() neq \"\"'>" +
            "   AND date_format (a.alarm_time,'%Y-%m-%d') &lt;=date_format(#{hmsFaceRecord.endDate},'%Y-%m-%d') " +
            "</if> " +
            " order by a.alarm_time desc"+
            "</script>")
    Page<HmsFaceRecord> page(HmsFaceRecord hmsFaceRecord, Page<HmsFaceRecord> page);
}
