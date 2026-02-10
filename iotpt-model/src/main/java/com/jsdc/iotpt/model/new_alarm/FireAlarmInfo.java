package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.SysUser;
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
@TableName("FIRE_ALARM_INFO")
@Table(name = "FIRE_ALARM_INFO")
@KeySequence(value = "SEQ_FIRE_ALARM_INFO_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "消防报警处理信息")
public class FireAlarmInfo extends Model<FireAlarmInfo> implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_FIRE_ALARM_INFO_ID", sequenceName = "SEQ_FIRE_ALARM_INFO_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_FIRE_ALARM_INFO_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "消防报警处理信息id")
    private Integer id;

    @ApiModelProperty(value = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hanlderTime;

    @ApiModelProperty(value = "处理结果")
    private String content;

    @ApiModelProperty(value = "处理人")
    private String hanlderUsers;

    @ApiModelProperty(value = "值班人")
    private String dutyUsers;

    @ApiModelProperty(value = "删除")
    private String isDel;


    @ApiModelProperty(value = "报警id")
    private String alarmId;

    @Transient
    @TableField(exist = false)
    private List<SysUser> hanlderUser;


    @Transient
    @TableField(exist = false)
    private List<SysUser> dutyUser;
}
