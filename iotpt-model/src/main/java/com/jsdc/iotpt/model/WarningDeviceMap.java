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


/**
 * @author lb
 * @description: 告警配置设备关联表
 * @date: 2023/5/8 17:55
 */
@Entity
@TableName("WARNING_DEVICE_MAP")
@Table(name = "WARNING_DEVICE_MAP")
@KeySequence(value = "SEQ_WARNING_DEVICE_MAP_ID")
@Data
@ApiModel(value = "告警配置设备关联表")
public class WarningDeviceMap implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WARNING_DEVICE_MAP_ID", sequenceName = "SEQ_WARNING_DEVICE_MAP_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WARNING_DEVICE_MAP_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "告警配置设备关联表主键id")
    private Integer id;

    @ApiModelProperty(value = "告警配置ID")
    private int warningConfigId;
    @ApiModelProperty(value = "设备ID")
    private int deviceId;


}
