package com.jsdc.iotpt.model;

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
@Entity
@TableName("PATROLTASK")
@Table(name = "PATROLTASK")
@KeySequence(value = "PATROL_TASK_ID")
@Data
public class PatrolTask extends Model<PatrolTask> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "patrolTaskId", sequenceName = "PATROL_TASK_ID", allocationSize = 1)
    @GeneratedValue(generator = "patrolTaskId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "计划ID")
    private String planId;



    @Transient
    @TableField(exist = false)
    private String userId;

    @Transient
    @TableField(exist = false)
    private String planName;

    @Transient
    @TableField(exist = false)
    private String planType;

    @Transient
    @TableField(exist = false)
    private String planRemark;

    @Transient
    @TableField(exist = false)
    private String realName;

    private String msg;

    @ApiModelProperty(value = "打卡次数")
    private Integer clockInCount;

    @ApiModelProperty(value = "实际打卡次数")
    private Integer clockNum;

    @ApiModelProperty(value = "任务类型 1视频巡更 2电子巡更")
    private String taskType;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "任务类型名称")
    private String taskTypeName;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务描述")
    private String remarks;

    @ApiModelProperty(value = "任务时间")
    private String taskTime;

    // 开始时间
    @ApiModelProperty(value = "开始时间")
    private String cycleStartTime;

    // 结束时间
    @ApiModelProperty(value = "结束时间")
    private String cycleEndTime;

    @ApiModelProperty(value = "任务状态 0待处理 1已处理 2超时未处理 3缺卡")
    private String taskStatus;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "任务状态名称")
    private String taskStatusName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "当前页")
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "每页展示数据大小")
    private Integer pageSize ;

    @ApiModelProperty(value = "排除隐患次数成功1 否则0")
    private Integer hiddenDangers;
    @ApiModelProperty(value = "成功告警次数成功1 否则0")
    private Integer successAlarm;

}
