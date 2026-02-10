package com.jsdc.iotpt.vo;

import lombok.Data;

/**
 * @Author ：苹果
 * @Description：统计各楼层区域用电
 * @Date ：2023/7/14 9:16
 * @Modified By：
 */
@Data
public class EnergyEvaluateByFloorVo {

    private String floor;//楼层

    private String floorName;//楼层名称

    private String toDay="0";//今天

    private String lastDay="0";//昨天

    private String toWeek="0";//本周

    private String lastWeek="0";//上周

    private String toMonth="0";//本月

    private String lastMonth="0";//上月

    private String toYear="0";//本年

    private String lastYear="0";//上年
}
