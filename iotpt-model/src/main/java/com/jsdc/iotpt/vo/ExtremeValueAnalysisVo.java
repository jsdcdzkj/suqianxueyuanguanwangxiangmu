package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

/**
 * @authon thr
 * @describe 极值分析数据
 */
@Data
public class ExtremeValueAnalysisVo {

    private String startTime;
    private String endTime;

    private String timeStr;

    // 能源类型 1.电 2.水 3.气
    private String energyType;

    /**
     * 参数类型
     * 1相电压
     * 2相电流
     * 3相有功功率
     * 4相无功功率
     * 5相视在功率
     * 6相端子温度
     * 7频率
     */
    private String paramType;

    // 时间类型 1.日 2 月
    private String timeType;

    // 名称
    private String name;
    private String maxVal;
    private String minVal;
    //平均值
    private String meanVal;
    // 发生时间
    private String maxOccurrenceTime;
    private String minOccurrenceTime;

    //设备id集合
    private List<String> deviceIds;

    private String deviceId;

}
