package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @projectName: IOT
 * @className: mission_assign
 * @author: zln
 * @description: 我的任务—任务指派信息表
 * @date: 2023/8/23 13:55
 */
@Entity
@TableName("MISSION_ASSIGN")
@Table(name = "MISSION_ASSIGN")
@KeySequence(value = "SEQ_MISSION_ASSIGN_ID")
@Data
@ApiModel(value = "任务指派信息表")
public class MissionAssign implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "missionAssignId", sequenceName = "SEQ_MISSION_ASSIGN_ID", allocationSize = 1)
    @GeneratedValue(generator = "missionAssignId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "任务指派信息表ID")
    private Integer id;
    //关联任务id
    @ApiModelProperty(value = "任务ID", required = true)
    private Integer missionId;
    @ApiModelProperty(value = "班组ID", required = true)
    private Integer teamGroupsId;
    //取字典（taskTypes）
    //1.抢修 2维修 3巡检 4保养
    @ApiModelProperty(value = "任务类型", required = true)
    private Integer taskType;
    //返回字段
    @TableField(exist = false)
    @Transient
    private String taskTypeName;
    //服务类型 1：公区服务 2：有偿服务
    @TableField(exist = false)
    @Transient
    private Integer serviceType;
    //班组名称 返回字段
    @TableField(exist = false)
    @Transient
    private String teamGroupsName;
    //紧急程度 返回字段
    @TableField(exist = false)
    @Transient
    private String urgencyLabel;
    //取字典（missionUrgency）
    @ApiModelProperty(value = "紧急程度", required = true)
    private Integer urgency;
    @ApiModelProperty(value = "处理期限", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deadline;
    @ApiModelProperty(value = "备注")
    private String notes;
    //0、过期  1、启用
    @ApiModelProperty(value = "状态", required = true)
    private Integer state;
    @ApiModelProperty(value = "任务计划名称")
    private String jobPlanName;
    //1、待指派；2、待处理；3、已处理  0、暂存(冗余来的)
    @ApiModelProperty(value = "任务状态")
    private Integer missionStates;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;
}
