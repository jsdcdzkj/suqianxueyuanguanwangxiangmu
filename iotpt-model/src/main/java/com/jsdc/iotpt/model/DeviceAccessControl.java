package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: DataService
 * Description: 数据服务
 * date: 2023/5/8 14:55
 *
 * @author zdq
 */
@Entity
@TableName("device_access_control")
@Table(name = "device_access_control")
@KeySequence(value = "SEQ_DEVICE_ACCESS_CONTROL_ID")
@Data
@ApiModel(value ="门禁设备")
public class DeviceAccessControl implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_DEVICE_ACCESS_CONTROL_ID", sequenceName = "SEQ_DEVICE_ACCESS_CONTROL_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DEVICE_ACCESS_CONTROL_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="数据服务id")
    private Integer id;

    @ApiModelProperty(value ="设备名称")
    private String name;
    @ApiModelProperty(value ="设备编码")
    private String deviceCode;
    @ApiModelProperty(value ="所属区域")
    private Integer areaId;
    @ApiModelProperty(value ="所属楼层")
    private Integer floorId;
    @ApiModelProperty(value ="所属楼宇")
    private Integer buildId;
    @ApiModelProperty(value ="设备型号")
    private Integer modelNum;
    @ApiModelProperty(value ="设备描述")
    private String deviceDesc;
    @ApiModelProperty(value ="供应商")
    private Integer supplierId;
    @ApiModelProperty(value ="设备状态")
    private Integer status;
    /**
     * 设备在线状态（1正常、2离线、3异常）
     */
    @ApiModelProperty(value = "设备在线状态")
    private String onLineStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String areaName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String floorName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String buildName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String supplierName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String modelName;

}
