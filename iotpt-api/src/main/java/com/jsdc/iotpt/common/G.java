package com.jsdc.iotpt.common;

import lombok.experimental.UtilityClass;

/**
 * @projectName: IOT
 * @className: G
 * @author: wp
 * @description: 静态类
 * @date: 2023/5/9 13:38
 */
@UtilityClass
public class G {
    /**
     * 逻辑删除 是
     */
    public Integer ISDEL_YES = 1;

    /**
     * 逻辑删除 否
     */
    public Integer ISDEL_NO = 0;

    public Integer NULL_PARENT_ID = 0;

    /**
     * kafka数据主题
     */
    public String KAFKA_DATA_TOPIC="DATA_TOPIC";
    /**
     *  kafka告警主题
     */
    public String KAFKA_ALERTING_TOPIC="ALERT_TOPIC";
    /**
     *  kafka心跳主题
     */
    public String KAFKA_HEARTBEAT_TOPIC="HEART_TOPIC";


    /**
     *  任务计划
     */
    public String JOB_PLAN="JOB_PLAN";

    /**
     *  单次巡更计划
     */
    public String PATROL_PLAN="PATROL_PLAN";

    /**
     *  多次巡更计划
     */
    public String PATROL_TIMES="PATROL_TIMES";


    /**
     * 应用平台标识,用于登录菜单过滤
     */
    public Integer APP_PLATFORM = 1;
    // 智慧应用平台标识
    public Integer APP_WISDOM = 2;
    // 物联网平台标识
    public Integer APP_PLATFORM_IOT = 3;
    // app
    public Integer WLW_APP = 4;
    // 平台标识
    public String PLATFORM = "iotpt-";

    public String CHANNELID_ELECTRICITY = "ENERGY_TOTAL";//用电
    public String CHANNELID_WATER = "WATER_L";//用水

    public Integer BUILD_ID = 121;//楼宇id


    public String DISH_TYPE_ALL = "0";//餐品类型字典 全部

    // 文件服务器标识 iotpt
    public String MINIO_BUCKET = "iotpt";
    // 告警视频文件专用桶
    public String MINIO_WARNING_VIDEO_BUCKET = "warnvideo";

    public String MINIO_BUCKET_LOCK = "iotptlock";

    public String CHANNELID_ELECTRICITY_NUM = "1";//用电
    public String CHANNELID_WATER_NUM = "4";//用水

}
