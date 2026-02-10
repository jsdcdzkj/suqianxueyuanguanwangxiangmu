package com.jsdc.iotpt.model.new_alarm;


import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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

@Entity
@TableName("ALARM_CONTENT_CONFIG")
@Table(name = "ALARM_CONTENT_CONFIG")
@KeySequence(value = "SEQ_ALARM_CONTENT_CONFIG_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警内容配置表")
public class AlarmContentConfig extends Model<AlarmContentConfig> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_CONTENT_CONFIG_ID", sequenceName = "SEQ_ALARM_CONTENT_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_CONTENT_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    @ExcelIgnore
    private Integer id;

    @ApiModelProperty(value = "告警内容")
    @ExcelProperty("告警内容")
    private String alarmContent;

    @ApiModelProperty(value = "告警编号")
    @ExcelProperty("告警编号")
    private String alarmCode;

    @ApiModelProperty(value = "告警等级，字典：alarmLevel")
    @ExcelIgnore
    private Integer alarmLevel;

    @ApiModelProperty(value = "告警等级")
    @Transient
    @TableField(exist = false)
    @ExcelProperty("告警等级")
    private String alarmLevelName;

    @ApiModelProperty(value = "告警类型，字典：alarmCategory")
    @ExcelIgnore
    private Integer alarmCategory;

    @ApiModelProperty(value = "告警类型")
    @Transient
    @TableField(exist = false)
    @ExcelProperty("告警类别")
    private String alarmCategoryName;

    @ApiModelProperty(value = "告警分组，字典：alarmGroup")
    @ExcelIgnore
    private Integer alarmGroup;

    @ApiModelProperty(value = "告警分组")
    @Transient
    @TableField(exist = false)
    @ExcelProperty("告警分组")
    private String alarmGroupLabel;

    @ApiModelProperty(value = "告警种类，字典：alarmType")
    @ExcelIgnore
    private Integer alarmType;

    @ApiModelProperty(value = "告警种类名称")
    @Transient
    @TableField(exist = false)
    @ExcelProperty("告警种类")
    private String alarmTypeName;

    @ApiModelProperty(value = "归属对象，数据库：CONFIG_DEVICE_TYPE表ID")
    @ExcelIgnore
    private Integer deviceType;

    @ApiModelProperty(value = "归属对象名称")
    @ExcelProperty("归属对象")
    private String deviceTypeName;

    @ApiModelProperty(value = "内容备注")
    @ExcelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "是否启用 0：未启用，1：启用")
    @ExcelIgnore
    private Integer enable;

    @ApiModelProperty(value = "是否启用汉字")
    @Transient
    @TableField(exist = false)
    @ExcelProperty("是否启用")
    private String enableName;

    @ApiModelProperty(value = "是否删除")
    @ExcelIgnore
    private Integer isDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelIgnore
    private Date createTime;

    @ExcelIgnore
    private Integer createUser;

    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ExcelIgnore
    private Integer updateUser;

    @ApiModelProperty(value = "多模态技能ID")
    @ExcelProperty("多模态技能ID")
    private String algId;

}
