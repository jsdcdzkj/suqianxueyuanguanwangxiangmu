package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@TableName("ALARM_CATEGORY")
@Table(name = "ALARM_CATEGORY")
@KeySequence(value = "SEQ_ALARM_CATEGORY_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警类型")
public class AlarmCategory {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_CATEGORY_ID", sequenceName = "SEQ_ALARM_CATEGORY_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_CATEGORY_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "类型名称")
    private String name;

    @ApiModelProperty(value = "类型编号")
    private String code;

    @ApiModelProperty(value = "告警分组，字典：alarmGroup")
    private Integer alarmGroup;

    @ApiModelProperty(value = "告警分组")
    @Transient
    @TableField(exist = false)
    private String alarmGroupName;

    @ApiModelProperty(value = "告警种类，字典：alarmType")
    private Integer alarmType;

    @ApiModelProperty(value = "告警种类")
    @Transient
    @TableField(exist = false)
    private String alarmTypeName;

    @ApiModelProperty(value = "图片文件地址")
    private String icon;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer updateUser;

}
