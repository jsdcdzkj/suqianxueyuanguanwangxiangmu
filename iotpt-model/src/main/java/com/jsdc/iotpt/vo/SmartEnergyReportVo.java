package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

/**
 * 智慧能源
 */
@Data
public class SmartEnergyReportVo {

    //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
    private String timeType;
    //统计维度：数据类型 1 用量 2 碳排放 3 标准煤 4 费用
    private String dataType;
    //统计维度：时间类型 1 小时 2 日
    private String dateType;
    //能源类型 1 电 2 水
    private String energyType;
    //设备类型 1 电压 2 电流
    private String deviceType;

    //开始时间
    private String startTime;
    //结束时间
    private String endTime;

    //设备id集合
    private List<Integer> collectIds;

    //设备分项id集合
    private Integer deviceSubitemId;
    private List<Integer> deviceSubitemIds;
    //楼宇id集合
    private Integer buildId;
    private List<Integer> buildIds;
    //楼层id集合
    private Integer floorId;
    private List<Integer> floorIds;
    //区域id集合
    private Integer areaId;
    private List<Integer> areaIds;
    //设备类型id集合
    private Integer deviceTypeId;
    private List<Integer> deviceTypeIds;
    //多时间段
    private List<String> timeList;

    //设备id
    private Integer deviceId;
    //最大值
    private String maxVal;
    //最小值
    private String minVal;

    //名称 x轴 value
    private String name;
    //值 y轴 value
    private String val;
    //名称 x轴 value
    private List<String> nameList;
    //值 y轴 value
    private List<String> valList;
    //等级
    private String level;
    //占比
    private String ratio;
}
