package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

/**
 * @authon thr
 * @describe 综合大屏
 */
@Data
public class EnergyReportVo {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    //逻辑位置-所属楼宇
    private List<Integer> logicalBuildIds;

    private Integer logicalFloorId;
    //信号编码
    private List<String> channelIds;
    //信号编码
    private String channelId;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;
    //采集设备id
    private Integer deviceCollectId;

    //aep设备id
    private Integer deviceAepId;

    //设备类型编码
    private String deviceTypeCode;

    /**
     * 类型 1用电 2用水 3用气
     * 类型 1室内温度 2室外温度 3室内湿度 4室外湿度
     */
    private String type;

    //时间类型 1 年度 2 月度 3 日  4 去年同月 5 去年同日
    private String timeType;

    //用电 年度
    private String powerYear;

    //用电 月度
    private String powerMonth;

    //用电 日
    private String powerDay;

    //用水 年度
    private String waterYear;

    //用水 月度
    private String waterMonth;
    //用水 去年同月
    private String lastYearWaterMonth;

    //用水 日
    private String waterDay;

    //用气 年度
    private String gasYear;

    //用气 月度
    private String gasMonth;

    //用气 日
    private String gasDay;

    //年度碳排放
    private String tocYear;
    //年度标准煤
    private String coalYear;

    //月度碳排放
    private String tocMonth;
    //月度标准煤
    private String coalMonth;

    //日度碳排放
    private String tocDay;
    //日度标准煤
    private String coalDay;

    //配置标识
    private String configSign;
    //设备ID集合
    private String[] deviceIds;
    private List<Integer> deviceIdList;

    //室内温度
    private String inTemp;
    //室外温度
    private String outTemp;
    //室内湿度
    private String inHumidity;
    //室外湿度
    private String outHumidity;
    //二氧化碳 CO2
    private String co2;
    //PM2.5 PM2.5
    private String pm;
    // PM_TEN	PM10
    private String pm_ten;
    // TVOC	VOC
    private String tvoc;
    // CH2O	甲醛
    private String nh3;
    // CO	一氧化碳
    private String co;
    // HCHO	氨气浓度
    private String hcho;

    //水压
    private String water_p;
    //流量
    private String water_fr;
    /**
     * 设备在线状态（1正常、2离线、3报警）
     */
    private String onLineStatus;
    /**
     * 设备编码 序列号
     */
    private String deviceCode;
    //楼层名称
    private String floorName;


    //用电量
    private String electricityYear;

    // U_A	A相电压
    private String electricityUAYear;

    // U_B	B相电压
    private String electricityUBYear;

    // U_C	C相电压
    private String electricityUCYear;

    // I_A	A相电流
    private String electricityIAYear;

    // I_B	B相电流
    private String electricityIBYear;

    // I_C	C相电流
    private String electricityICYear;

    // P_A	A相有功功率
    private String electricityPAYear;

    // P_B	B相有功功率
    private String electricityPBYear;

    // P_C	C相有功功率
    private String electricityPCYear;


    private String electricityTotal ;
    private String year ;




}
