package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

/**
 * @authon thr
 * @describe 电气安全统计
 */
@Data
public class ElectricalSafetyReportVo {
    private Integer pageIndex;
    private Integer pageSize;

    private String startTime;
    private String endTime;

    private Integer buildId;//楼宇ID
    private Integer floorId;//楼层ID
    private Integer areaId;//区域ID
    private String level;//告警等级
    private String levelName;//告警等级
    private Integer states; //1、待指派；2、待处理；3、已处理  0、暂存

    private List<String> valList;//数据集合
    private List<String> nameList;//名称集合
    private Integer count;
    private Integer dzpCount;
    private Integer dclCount;
    private Integer yclCount;
    private String realName;
    private String phone;
    private String location;
    private String place;
    private String deviceId;

}
