package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@TableName("ALARM_TYPE_CONFIG")
@Table(name = "ALARM_TYPE_CONFIG")
@KeySequence(value = "SEQ_ALARM_TYPE_CONFIG_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警类型配置表")
public class AlarmTypeConfig extends Model<AlarmTypeConfig> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_TYPE_CONFIG_ID", sequenceName = "SEQ_ALARM_TYPE_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_TYPE_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "告警类型配置表主键id")
    private Integer id;


    //第三方设备唯一标识字段
    @ApiModelProperty(value = "第三方设备唯一标识字段")
    private  Integer deviceIdField;

    //设备来源
    @ApiModelProperty(value = "设备来源")
    private  Integer deviceSource;

    //字段类型
    @ApiModelProperty(value = "字段类型")
    private  Integer fieldType;

    //字段名
    @ApiModelProperty(value = "字段名")
    private  String fieldName;

    //条件
    @ApiModelProperty(value = "条件")
    private  String condition;

    //字段值
    @ApiModelProperty(value = "字段值")
    private  String fieldValueMin;

    //字段值
    @ApiModelProperty(value = "字段值")
    private  String fieldValueMax;

    /**
     * "人员告警
     * 厨房告警
     * 车辆告警
     * 设备运维告警
     * 消防告警
     * 电气告警
     * 门禁告警
     * 能耗预警"
     */
    //告警类型
    @ApiModelProperty(value = "告警类型")
    private  Integer alarmCategory;

    //告警详细类别
    @ApiModelProperty(value = "告警详细类别")
    private  Integer alarmType;

    //告警级别
    @ApiModelProperty(value = "告警级别")
    private  Integer alarmLevel;



}
