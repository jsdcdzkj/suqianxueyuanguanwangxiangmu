package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

/**
 * 泰杰赛智慧照明系统 点位实时数据
 */

@Data
public class PointRealtimeDataDto {

    /**
     * 设备点位Id
     */
    private String pointId;

    /**
     * 数据更新时间,与当前时间的时间间隔
     */
    private String timeSpan;

    /**
     * 时间
     */
    private String time;

    /**
     * 值（double类型）
     */
    private String value;

    /**
     * 实际存储值string类型
     */
    private String content;

}
