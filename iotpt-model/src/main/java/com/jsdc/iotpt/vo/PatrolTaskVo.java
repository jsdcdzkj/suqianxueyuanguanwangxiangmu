package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 巡更任务
 */
@KeySequence(value = "PATROL_TASK_ID")
@Data
public class PatrolTaskVo extends Model<PatrolTaskVo> {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "计划ID")
    private String planId;



    @ApiModelProperty(value = "打卡次数")
    private Integer clockInCount;

    @ApiModelProperty(value = "实际打卡次数")
    private Integer clockNum;

    @ApiModelProperty(value = "任务类型 1视频巡更 2电子巡更")
    private String taskType;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务描述")
    private String remarks;

    @ApiModelProperty(value = "任务时间")
    private String taskTime;

    @ApiModelProperty(value = "任务状态 0待处理 1已处理 2超时未处理 3缺卡")
    private String taskStatus;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "当前页")
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "每页展示数据大小")
    private Integer pageSize ;


}
