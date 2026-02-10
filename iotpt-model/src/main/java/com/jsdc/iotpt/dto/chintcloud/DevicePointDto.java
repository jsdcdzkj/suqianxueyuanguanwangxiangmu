package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;


/**
 * 泰杰赛智慧照明系统 点位数据
 */

@Data
public class DevicePointDto {

    /**
     * 网关点位查数id，注意：查询点位的实时值、历史值还有做点位控制用这个id，所以一般要取这个id。注意！
     */
    private String storagePoint;

    /**
     * 网关设备id
     */
    private String deviceId;

    private Integer dataByteOrder;

    /**
     * 网关点位名称
     */
    private String pointName;

    /**
     * 网关点位标识符
     */
    private String pointKey;

    private Integer dataType;

    private Integer saveMode;

    private Integer saveTimeSleep;

    private Integer saveThreshold;

    private Integer max;

    private Integer min;

    private Integer readWriteMode;

    private Integer trendType;

    private Integer changeLimit;

    private String tags;

    private String addTime;

    private String modifyTime;

    /**
     * 网关点位id，注意，对点位进行增删改查用这个id
     */
    private String id;

    private Long sort;

    private String remark;

}
