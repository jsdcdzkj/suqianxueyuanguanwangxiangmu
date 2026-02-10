package com.jsdc.iotpt.model.operate;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@TableName("ABNORMAL_PATROL")
@Table(name = "ABNORMAL_PATROL")
@KeySequence(value = "SEQ_ABNORMAL_PATROL_ID")
@Data
@ApiModel(value = "巡检异常")
public class AbnormalPatrol implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ABNORMAL_PATROL_ID", sequenceName = "SEQ_ABNORMAL_PATROL_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ABNORMAL_PATROL_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "巡检异常id")
    private Integer id;

    @ApiModelProperty(value = "任务ID")
    private Integer missionId;

    //字典 1. 一般 2.紧急 3.严重
    @ApiModelProperty(value = "异常等级")
    private Integer abnormalLevel;

    @ApiModelProperty(value = "异常描述")
    private String abnormalDescription;

    @ApiModelProperty(value = "异常对象Id")
    private Integer deviceId;

    @ApiModelProperty(value = "异常对象名称")
    private String deviceName;

    @ApiModelProperty(value = "异常时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date abnormalTime;

    // 0. 待处理 1.处理中 2.已处理, 3.已忽略
    @ApiModelProperty(value = "处理结果")
    private Integer handleResult;

    @ApiModelProperty(value = "备注")
    private String comments;

    // 1:已忽略  2:已上报
    @ApiModelProperty(value = "操作")
    private Integer operation;

    // 选择上报时生成的任务级别
    @ApiModelProperty(value = "generatedMissionLevel")
    private Integer generatedMissionLevel;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String jobPlanName;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer teamGroupId;

//    @Transient
//    @TableField(exist = false)
//    @ApiModelProperty(hidden = true)
//    private String teamGroupName;


    // 任务类型: 1.巡检 2.保养
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer taskType;

    // 任务类型名称
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String taskTypeName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer missionStatesId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String missionStatesName;
}
