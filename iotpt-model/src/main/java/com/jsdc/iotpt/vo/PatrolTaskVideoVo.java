package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.PatrolClock;
import com.jsdc.iotpt.model.PatrolReport;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PatrolTaskVideoVo {

    @ApiModelProperty(value = "任务状态 0待处理 1已处理 2超时未处理 3缺卡")
    private String taskStatus;
    @ApiModelProperty(value = "实际打卡次数")
    private Integer clockNum;

    // 开始时间
    @ApiModelProperty(value = "开始时间")
    private String cycleStartTime;

    // 结束时间
    @ApiModelProperty(value = "结束时间")
    private String cycleEndTime;

    @ApiModelProperty(value = "计划名称")
    private String planName;
    @ApiModelProperty(value = "计划类型")
    private String planType;
    @ApiModelProperty(value = "计划状态")
    private Integer planStatus;
    @ApiModelProperty(value = "执行类型")
    private String executeType;
    @ApiModelProperty(value = "执行周期")
    private String planCycle;
    @ApiModelProperty(value = "执行时间 单次yyyy-MM-dd HH:mm:ss 多次格式00:00,01:30...")
    private String cycleTime;
    @ApiModelProperty(value = "执行日期 单次 空  多次 1,2,3.。。。。。")
    private String cycleDate;
    @ApiModelProperty(value = "计划描述")
    private String planRemark;
    @ApiModelProperty(value = "打卡次数")
    private Integer clockInCount;

    @ApiModelProperty(value = "设备ID")
    private String deviceId;
    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "安装位置")
    private String place;

    /**
     * 所属区域
     */
    @ApiModelProperty(value = "所属区域")
    private Integer areaId;

    /**
     * 所属楼层
     */
    @ApiModelProperty(value = "所属楼层")
    private Integer floorId;

    /**
     * 所属楼宇
     */
    @ApiModelProperty(value = "所属楼宇")
    private Integer buildId;

    //物理位置
    private String areaNames;

    private List<PatrolClock> patrolClockList;

    private List<PatrolReport> patrolReportList;

}
