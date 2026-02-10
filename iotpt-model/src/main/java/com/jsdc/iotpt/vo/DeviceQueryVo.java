package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import lombok.Data;

import java.util.List;

@Data
public class DeviceQueryVo {
    private String deviceId;//设备ID
    private String deviceName;//设备名称
    private String deviceCode;//设备名称
    private String energyType;//能源类型
    private Integer buildId;//楼宇ID
    private List<Integer> buildIds;
    private Integer floorId;//楼层ID
    private List<Integer> floorIds;
    private Integer areaId;//楼宇ID
    private List<Integer> areaIds;
    private String deviceType;//设备类型
    private String signalTypeCode;//信号类型
    private String channelId;//信号ID

    // influx分组
    private String groupStr;
    private Integer pageNo;
    private Integer pageSize;

    private String timeType;//1:日 2：月 3：年 4 周
    private String timeStr;//选择月或年时，用来存储月份和年份

    private String timeStart;//开始时间
    private String timeEnd;//结束时间
    private String onLineStatus;//设备在线状态（1正常、2离线、3异常）

    private List<DeviceCollect> deviceCollects ;

    private List<DeviceCollectVo> deviceCollectVos ;

    private Integer subitemId;//所属分项
    private Integer logicalAreaId;//逻辑位置-所属区域
    private Integer logicalFloorId;//逻辑位置-所属楼层
    private Integer logicalBuildId;//逻辑位置-所属楼宇
    //对比开始时间
    private String compareStart ;

    //对比结束时间
    private String compareEnd ;


    //能耗预测 选中数据
    private String selectData ;

    //能耗预测 历史数据
    private String oldYears ;

    //楼层
    private List<SysBuildFloor> sysBuildFloorList ;


    //类型  0 正常查时间段内的电量  1 趋势数据  当前日期前7天数据
    private String type  ;
}
