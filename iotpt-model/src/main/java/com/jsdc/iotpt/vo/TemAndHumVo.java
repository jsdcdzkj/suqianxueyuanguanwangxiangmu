package com.jsdc.iotpt.vo;

import lombok.Data;

@Data
public class TemAndHumVo {

    //温度
    private double temperature;

    //湿度
    private double humidity;

    private double val;

    private Integer areaId;
}
