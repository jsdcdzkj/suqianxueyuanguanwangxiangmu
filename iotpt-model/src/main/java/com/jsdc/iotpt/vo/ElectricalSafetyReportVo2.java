package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import lombok.Data;

import java.util.List;

@Data
public class ElectricalSafetyReportVo2 {

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
    private String deviceName;//设备名称
    private String deviceTypeName;//设备类型
    private Integer deviceTypeId;//设备类型

    private Integer deviceNum; //设备总数
    private Integer warningNum; //报警总数
    private String areaName; //商户名称
    private Integer qwNum; //轻微
    private Integer ybNum; //一般
    private Integer yzNum; //严重
    private String subitemNum; //分项
    private String businessesIds; //分项
    private Page<SysBuildArea> page; //分项

}
