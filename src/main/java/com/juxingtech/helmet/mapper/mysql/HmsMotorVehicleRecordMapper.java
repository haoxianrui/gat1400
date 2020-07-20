package com.juxingtech.helmet.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juxingtech.helmet.entity.HmsMotorVehicleRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author haoxr
 * @date 2020-07-06
 **/
@Mapper
public interface HmsMotorVehicleRecordMapper extends BaseMapper<HmsMotorVehicleRecord> {

    @Select("<script>" +
            " SELECT  a.*, b.NAME helmet_name,b.serial_no " +
            " FROM " +
            " hms_motor_vehicle_record a " +
            " LEFT JOIN hms_helmet b ON a.device_id = b.img_device_id" +
            " where 1=1 " +
            "<if test='hmsMotorVehicleRecord.helmetName!=null and hmsMotorVehicleRecord.helmetName.trim() neq \"\"'>" +
            "   AND b.name like concat('%',#{hmsMotorVehicleRecord.helmetName},'%') " +
            "</if>"+
            "<if test='hmsMotorVehicleRecord.serialNo!=null and hmsMotorVehicleRecord.serialNo.trim() neq \"\"'>" +
            "   AND b.serial_no like concat('%',#{hmsMotorVehicleRecord.serialNo},'%') " +
            "</if>"+
            "<if test='hmsMotorVehicleRecord.startDate!=null and hmsMotorVehicleRecord.startDate.trim() neq \"\"'>" +
            "   AND date_format (a.alarm_time,'%Y-%m-%d') &gt;=date_format(#{hmsMotorVehicleRecord.startDate},'%Y-%m-%d') " +
            "</if>"+
            "<if test='hmsMotorVehicleRecord.endDate!=null and hmsMotorVehicleRecord.endDate.trim() neq \"\"'>" +
            "   AND date_format (a.alarm_time,'%Y-%m-%d') &lt;=date_format(#{hmsMotorVehicleRecord.endDate},'%Y-%m-%d') " +
            "</if>" +
            " order by a.alarm_time desc"+
            "</script>")
    Page<HmsMotorVehicleRecord> page(HmsMotorVehicleRecord hmsMotorVehicleRecord, Page<HmsMotorVehicleRecord> page);
}
