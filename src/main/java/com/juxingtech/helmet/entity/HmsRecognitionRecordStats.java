package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

@Data
public class HmsRecognitionRecordStats {

    @TableId
    private Long id;

    private String date;

    private Integer faceRecordCount;

    private Integer motorVehicleRecordCount;

}
