package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.DataSheet;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @authon thr
 * @describe 设备采集数据
 */
@Data
public class DataSheetVo extends DataSheet {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String startTime;
    private String endTime;

    private String timeStr;


    private List<String> codes;

    // 总功率
    private String totalPower;

    // 能源类型 1.电 2.水 3.气
    private Integer energyType;

    // 设备类型
    private Integer deviceType;

    // 时间类型 1.日 2 月  3年 4.周
    private Integer timeType;

    // 是否是总设备 0否 1是
    private Integer isTotalDevice;

    // 现在
    private Date thisDate;

    // influx数据库返回值
    private String influxVal;

    // influx分组
    private String groupStr;

    // 今日x
    private LinkedHashMap<String,String> thisXData;

    // 昨日x
    private LinkedHashMap<String,String> lastXData;

    // 去年x
    private LinkedHashMap<String,String> sameXData;

    private List<SysBuildFloor> floors;
    //楼层集合
    private  List<Integer> floorIdList = new ArrayList<>();


    private Integer instanceType;

    private List<Integer> buildIds;
    private List<Integer> floorIds;
    private List<Integer> areaIds;
}
