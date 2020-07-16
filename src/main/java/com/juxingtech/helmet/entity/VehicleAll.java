package com.juxingtech.helmet.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("TRFF_QUERY.VEHICLE_ALL")
public class VehicleAll {

    private String hpzl;

    private String hphm;

    private String clpp1;

    private String clsbdh;

    private String cllx;

    private String csys;

    private String syxz;

    private String zt;

}
