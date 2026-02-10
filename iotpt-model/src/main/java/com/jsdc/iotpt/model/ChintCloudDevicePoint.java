package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 泰杰赛智慧照明系统  点位信息
 */
@Entity
@TableName("CHINT_CLOUD_DEVICE_POINT")
@Table(name = "CHINT_CLOUD_DEVICE_POINT")
@KeySequence(value = "CHINT_CLOUD_DEVICE_POINT_ID")
@Data
public class ChintCloudDevicePoint extends Model<ChintCloudDevicePoint> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "CHINT_CLOUD_DEVICE_POINT_ID", sequenceName = "CHINT_CLOUD_DEVICE_POINT_ID", allocationSize = 1)
    @GeneratedValue(generator = "CHINT_CLOUD_DEVICE_POINT_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "所属楼宇")
    private Integer buildId;

    @Transient
    @TableField(exist = false)
    private String buildName;

    @ApiModelProperty(value = "所属楼层")
    private Integer floorId;

    @Transient
    @TableField(exist = false)
    private String floorName;

    @ApiModelProperty(value = "所属区域")
    private Integer areaId;

    @Transient
    @TableField(exist = false)
    private String areaName;

    @ApiModelProperty(value = "设备类型")
    private String type;

    @ApiModelProperty(value = "点位号")
    private String storagePoint;

    /**
     * 网关设备id
     */
    @ApiModelProperty(value = "网关设备ID")
    private String deviceId;

    private Integer dataByteOrder;

    /**
     * 网关点位名称
     */
    @ApiModelProperty(value = "点位名称")
    private String pointName;

    /**
     * 网关点位标识符
     */
    private String pointKey;

    private Integer dataType;

    private Integer saveMode;

    private Integer saveTimeSleep;

    private Integer saveThreshold;

    private Integer max;

    private Integer min;

    private Integer readWriteMode;

    private Integer trendType;

    private Integer changeLimit;

    private String tags;

    private String addTime;

    private String modifyTime;

    /**
     * 网关点位id，注意，对点位进行增删改查用这个id
     */
    private String chintCloudId;

    private Long sort;

    private String remark;

    //开启状态
    @Transient
    @TableField(exist = false)
    private Boolean open;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;

    /**
     * 是否总开 0:否  1：是
     */
    @ApiModelProperty(value = "是否总开 0:否  1：是")
    private Integer isMaster;

    @Transient
    @TableField(exist = false)
    private String typeName;

    @Transient
    @TableField(exist = false)
    private String fileName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "场景 0:无  1：开启 2：关闭")
    private Integer sceneLinkage;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "雷达 0:关闭  1：开启")
    private Integer radar;

    /**
     * 运行功率 kw
     */
    @ApiModelProperty(value = "运行功率")
    private String operatingPower;

    /**
     * 运行时长 h
     */
    @ApiModelProperty(value = "运行时长")
    private String RunningTime;

    /**
     * 预计能耗 kwh
     */
    @ApiModelProperty(value = "预计能耗")
    private String expectedEnergy;


    /**
     *  灯光状态
     */
    @Transient
    @TableField(exist = false)
    private String value;

}
