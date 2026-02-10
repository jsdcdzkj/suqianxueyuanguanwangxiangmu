package com.jsdc.iotpt.vo;

import lombok.Data;

@Data
public class ParkingVo {
    private String parkSyscode;// 停车库唯一标识
    private String entranceSyscode;// 出入口唯一标识
    private String plateNo;// 车牌号
    private String cardNo;// 卡号
    private String startTime;// 查询开始时间 yyyy-MM-ddTHH:mm:ss+
    private String endTime;// 查询结束时间 yyyy-MM-ddTHH:mm:ss+
    private Integer vehicleOut;// 进出场标识 0-进场 1-出场
    private Integer vehicleType;// 车辆类型 0：其他车 1：小型车 2：大型车 3：摩托车
    private Integer releaseResult;// 放行结果 0-未放行  1-正常放行  2-离线放行
    private Integer releaseWay;// 放行方式  10-未开闸 11-自动开闸 12-人工/人工开闸 13-遥控器开闸
    private Integer releaseReason;// 放行原因 100-固定车自动放行 101-临时车自动放行 102-预约车自动放行 103-一户多车自动放行
    private String carCategory;// 车辆分类 9-黑名单 10-固定车  11-临时车 12-预约车  14-特殊车
    private Integer pageNo;// 目标页码
    private Integer pageSize;// 每页记录数
    private String queryType;//查询标识（用于标识接口以哪种方式查询，1：车牌查询 2：车位查询 3：停车时间段查询 4：无牌车停车查询）
    private String spaceNo;//车位号

}
