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
 * 网关设备(device_gateway)
 * 曹孙翔
 */
@Entity
@TableName("device_gateway")
@Table(name = "device_gateway")
@KeySequence(value = "SEQ_DEVICE_GATEWAY_ID")
@Data
@ApiModel(value ="网关设备")
public class DeviceGateway implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "deviceGatewayId", sequenceName = "SEQ_DEVICE_GATEWAY_ID", allocationSize = 1)
    @GeneratedValue(generator = "deviceGatewayId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="网关设备id")
    //主键
    private  Integer id;
    //设备名称
    @ApiModelProperty(value ="设备名称")
    private String name;
    //品牌
    @ApiModelProperty(value ="品牌")
    private String brand;
    //设备型号
    @ApiModelProperty(value ="设备型号id")
    private Integer modelId;
    //出厂编号
    @ApiModelProperty(value ="出厂编号")
    private String serialNum;
    //供应商
    @ApiModelProperty(value ="供应商id")
    private Integer supplierId;
    //设备编码
    @ApiModelProperty(value ="设备编码")
    private String deviceCode;
    //所属楼层
    @ApiModelProperty(value ="所属楼层")
    private Integer floorId;
    //所属楼宇
    @ApiModelProperty(value ="所属楼宇")
    private Integer buildId;
    //所属区域
    @ApiModelProperty(value ="所属区域")
    private Integer areaId;
    //安装位置
    @ApiModelProperty(value ="安装位置")
    private String place;
    //设备描述
    @ApiModelProperty(value ="设备描述")
    private String deviceDesc;
    //传输协议
    @ApiModelProperty(value ="传输协议id")
    private Integer configProtocolId;
    //服务连接
    @ApiModelProperty(value ="服务连接id")
    private Integer configLinkId;
    //发布主题
    @ApiModelProperty(value ="发布主题id")
    private Integer publishTopicId;
    //订阅主题
    @ApiModelProperty(value ="订阅主题id")
    private Integer subscribeTopicId;
    //告警主题
    @ApiModelProperty(value ="告警主题id")
    private Integer alarmTopicId;
    //心跳主题
    @ApiModelProperty(value ="心跳主题id")
    private Integer heartTopicId;
    //转换模板id
    @ApiModelProperty(value ="转换模板id")
    private Integer transferId;

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
    private String address;
    // 区域名称
    @Transient
    @TableField(exist = false)
    private String areaName;
}
