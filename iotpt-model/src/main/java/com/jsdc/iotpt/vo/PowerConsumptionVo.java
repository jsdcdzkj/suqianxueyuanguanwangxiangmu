package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PowerConsumptionVo {

    //月总用电量
    private Integer month_kWh;

    //月总碳排
    private Integer month_t;
    //月总标煤
    private Integer month_tce;

    //0：日 1：周  2：月 3：年 (参数不能为空)
    private Integer type;

    //楼层（接收参数不能为空）
    private List<Integer> floorList = new ArrayList<>();

    //楼宇Id
    private Integer buildId;


    private Double spread;


    private Date time;

    private List<String> xValue = new ArrayList<>();

    private List<Double> yValue1 = new ArrayList<>();

    private List<Double> yValue2 = new ArrayList<>();
}
