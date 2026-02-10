package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * ClassName: 巡更设备或点位
 * Description:
 * date: 2024/1/9 15:28
 *
 * @author bn
 */
@Entity
@TableName("PATROL_DEVICE_OR_POINT")
@Table(name = "PATROL_DEVICE_OR_POINT")
@KeySequence(value = "PATROL_DEVICE_OR_POINT_ID")
@Data
@ApiModel(value = "巡更人员")
public class PatrolDeviceOrPoint {


    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PATROL_DEVICE_OR_POINT_ID", sequenceName = "PATROL_DEVICE_OR_POINT_ID", allocationSize = 1)
    @GeneratedValue(generator = "PATROL_DEVICE_OR_POINT_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键")
    private Integer id;

    // 计划id
    private Integer planId;

    // 点位id
    private Integer pointId;

    // 设备id
    private Integer deviceId;

    // 设备名称
    @Transient
    @TableField(exist = false)
    private String deviceName;


    // 设备位置
    @Transient
    @TableField(exist = false)
    private String deviceLocation;
    // 时长
    private String duration;

    // 排序号
    private Integer sort;


    private Integer isDel;






}
