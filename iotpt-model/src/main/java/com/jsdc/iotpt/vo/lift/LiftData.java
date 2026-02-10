package com.jsdc.iotpt.vo.lift;

import lombok.Data;

import java.io.Serializable;

@Data
public class LiftData implements Serializable {


    private static final long serialVersionUID = 1L;
    private String deviceCode;

    private String deviceSn;

    private String status;//状态

    private Long uploadTime;

    private String door;//开门状态 0表示未知，1表示正在关门，2表示关门到位，3表示正在开门，4表示开门到位，5表示门锁锁止，6表示保持不完全关闭状态, 7表示上一个状态为干扰

    private String doorLock;//门锁

    private String passengerIn;//有无乘客

    private String workMode;//工作状态

    private String level;

    private String motion;//0（停止）/ 1（上行）/ -1（下行）/ 其他（未知）

    private Float opencloseDoorNumVal;//开关次数

    private String cumulativeRunTimeVal;//运行时间

    private String cumulativeRunNumVal;//运行时间

    private Float cumulativeRunTimeFVal;//运行时间

    private Float cumulativeRunNumFVal;//运行时间

    private Float workModeVal;//工作状态

    private Float mileageVal;//历程

    private Float floorVal;//楼层

    private Float levelVal;

    private Float motionVal;//运动状态

    private Float velocityVal;//速度

    private Float passengerInVal;//有无乘客

    private Float doorVal;//开门状态

    private Float statusVal;//电梯状态
}
