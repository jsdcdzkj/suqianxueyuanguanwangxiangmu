package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@TableName("ALARM_PLAN_CONFIG")
@Table(name = "ALARM_PLAN_CONFIG")
@KeySequence(value = "SEQ_ALARM_PLAN_CONFIG_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警预案配置表")
public class AlarmPlanConfig extends Model<AlarmPlanConfig> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_PLAN_CONFIG_ID", sequenceName = "SEQ_ALARM_PLAN_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_PLAN_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "预案名称")
    private String planName;

    @ApiModelProperty(value = "时间计划ID")
    private Integer planTimeConfigId;

    @ApiModelProperty(value = "时间计划名称")
    @Transient
    @TableField(exist = false)
    private String planTimeConfigName;

    @ApiModelProperty(value = "告警内容ID，英文逗号分隔")
    private String alarmContent;

    @ApiModelProperty(value = "告警内容ID列表")
    @Transient
    @TableField(exist = false)
    private List<String> alarmContentList;

    @ApiModelProperty(value = "告警内容名称，英文逗号分隔")
    @Transient
    @TableField(exist = false)
    private String alarmContentStr;

    @ApiModelProperty(value = "联动规则，英文逗号分隔，字典：alarmLinkageRule")
    private String linkageRule;

    @ApiModelProperty(value = "联动规则val列表")
    @Transient
    @TableField(exist = false)
    private List<String> linkageRuleList;

    @ApiModelProperty(value = "推送配置val，英文逗号分隔，字典：alarmPushConfig")
    private String pushConfig;

    @ApiModelProperty(value = "推送配置val列表")
    @Transient
    @TableField(exist = false)
    private List<String> pushConfigList;

    @ApiModelProperty(value = "推送方式，字典：alarmPushType")
    private Integer pushType;

    @ApiModelProperty(value = "推送人员或组或部门ID，英文逗号分隔")
    private String pushUser;

    @ApiModelProperty(value = "推送人员或组或部门ID列表")
    @Transient
    @TableField(exist = false)
    private List<String> pushUserList;

    @ApiModelProperty(value = "是否启用，0：未启用，1：启用")
    private Integer enable;

    @ApiModelProperty(value = "是否删除")
    private  Integer isDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer createUser;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer updateUser;

}
