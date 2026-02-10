package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: PatrolTimes
 * Description:
 * date: 2024/1/11 15:16
 *
 * @author bn
 */
@Entity
@TableName("PATROL_TIMES")
@Table(name = "PATROL_TIMES")
@KeySequence(value = "PATROL_TIMES_ID")
@Data
@ApiModel(value = "巡更时间段")
public class PatrolTimes implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PATROL_TIMES_ID", sequenceName = "PATROL_TIMES_ID", allocationSize = 1)
    @GeneratedValue(generator = "PATROL_TIMES_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "巡更计划id")
    private Integer planId;

    // 表达式
    @ApiModelProperty(value = "cron表达式")
    private String cronExpression;

    // 开始时间
    @ApiModelProperty(value = "开始时间")
    private String cycleStartTime;

    // 结束时间
    @ApiModelProperty(value = "结束时间")
    private String cycleEndTime;

    @ApiModelProperty(value = "删除标志:0,正常；1，删除")
    private Integer isDel;






}
