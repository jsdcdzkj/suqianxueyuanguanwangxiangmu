package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.vo.AlarmPlanTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@TableName("ALARM_PLAN_TIME_CONFIG")
@Table(name = "ALARM_PLAN_TIME_CONFIG")
@KeySequence(value = "SEQ_ALARM_PLAN_TIME_CONFIG_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警预案时间计划模板配置表")
public class AlarmPlanTimeConfig {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_PLAN_TIME_CONFIG_ID", sequenceName = "SEQ_ALARM_PLAN_TIME_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_PLAN_TIME_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "周一，时间段JSON")
    private String monday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> mondayList;

    @ApiModelProperty(value = "周二，时间段JSON")
    private String tuesday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> tuesdayList;

    @ApiModelProperty(value = "周三，时间段JSON")
    private String wednesday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> wednesdayList;

    @ApiModelProperty(value = "周四，时间段JSON")
    private String thursday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> thursdayList;

    @ApiModelProperty(value = "周五，时间段JSON")
    private String friday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> fridayList;

    @ApiModelProperty(value = "周六，时间段JSON")
    private String saturday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> saturdayList;

    @ApiModelProperty(value = "周日，时间段JSON")
    private String sunday;

    @Transient
    @TableField(exist = false)
    private List<AlarmPlanTimeVO> sundayList;

    @ApiModelProperty(value = "是否删除")
    private  Integer isDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer updateUser;


}
