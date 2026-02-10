package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName: JobPlan
 * Description: 任务计划
 * date: 2023/8/23 8:47
 *
 * @author bn
 */
@Entity
@TableName("job_plan")
@Table(name = "job_plan")
@KeySequence(value = "SEQ_JOB_PLAN_ID")
@Data
@ApiModel(value = "任务计划")
public class JobPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "jobPlanId", sequenceName = "SEQ_JOB_PLAN_ID", allocationSize = 1)
    @GeneratedValue(generator = "jobPlanId", strategy = GenerationType.SEQUENCE)
    private Integer id;
    //计划名称
    private String planName;
    //计划类型 字典类型planType 1.保养 2.巡检
    private Integer planType;
    // 类型名称
    @Transient
    @TableField(exist = false)
    private String typeName;
    //执行类型 1.重复执行 2.单次执行
    private Integer executeType;
    //计划状态 0:启用 1.停用
    private Integer planStatus;
    //计划开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planStartTime;
    //计划结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planEndTime;
    // 执行班组
    private Integer groupId;
    // 班组名称
    @Transient
    @TableField(exist = false)
    private String groupName;
    //执行周期 1.日，2.周，3.月
    private Integer executeCycle;
    //周期时间 根据执行周期，日（0-23），周（1-7），月（1-31）；
    private String cycleTime;

    // 级别 1.一般 2.紧急 3.重要
    private Integer planLevel;
//    //执行时长
//    private String executeTimes;
//    //执行时长单位 1.分钟，2.小时，3.天
//    private Integer executeTimeUnit;
    //cron表达式
    private String cronExpression;

    //执行标准id 执行检查项
    private Integer manageId;

    // 删除标志
    private Integer isDel;
    // 创建时间
    private Date createTime;

    // 执行时长
    private Integer executeTimes;

    // 执行单位 1.小时，2.天
    private Integer executeUint;

    private Integer createUser;

    @Transient
    @TableField(exist = false)
    private List<JobPlanArea> areaList;
}
