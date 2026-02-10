package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName: 巡更计划
 * Description:
 * date: 2024/1/8 16:28
 *
 * @author bn
 */
@Entity
@TableName("PATROL_PLAN")
@Table(name = "PATROL_PLAN")
@KeySequence(value = "PATROL_PLAN_ID")
@Data
@ApiModel(value = "巡更计划")
public class PatrolPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PATROL_PLAN_ID", sequenceName = "PATROL_PLAN_ID", allocationSize = 1)
    @GeneratedValue(generator = "PATROL_PLAN_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键")
    private Integer id;

    // 计划名称
    @ApiModelProperty(value = "计划名称")
    private String planName;

    // 计划类型 1.视频巡更
    @ApiModelProperty(value = "计划类型")
    private String planType;

    // 计划状态 0:开启 1 停用
    @ApiModelProperty(value = "计划状态 0:开启 1 停用")
    private Integer planStatus;

    // 表达式
    @ApiModelProperty(value = "表达式")
    private String cronExpression;

    // 执行类型 0:单次 1:循环
    @ApiModelProperty(value = "执行类型 0:单次 1:循环")
    private Integer executeType;

    // 执行周期 0:日 1:月 2:周
    @ApiModelProperty(value = "执行周期 0:日 1:月 2:周")
    private Integer planCycle;

    // 执行开始时间 单次使用yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "执行开始时间 单次使用yyyy-MM-dd HH:mm:ss")
    private Date cycleStartTime;

    //执行开始时间 单次使用yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "执行结束时间 单次使用yyyy-MM-dd HH:mm:ss")
    private Date cycleEndTime;

    // 执行日期 单次 空  多次 1,2,3.。。。。。
    @ApiModelProperty(value = "执行日期 单次 空  多次 1,2,3.。。。。。")
    private String cycleDate;

    // 计划描述
    @ApiModelProperty(value = "计划描述")
    private String planRemark;

    // 打卡次数
    @ApiModelProperty(value = "打卡次数")
    private Integer clockInCount;

    @ApiModelProperty(value = "删除标志:0,正常；1，删除")
    private Integer isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    private Integer createUser;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "修改人")
    private Integer updateUser;


    @Transient
    @TableField(exist = false)
    private List<PatrolTimes> patrolTimes;



}
