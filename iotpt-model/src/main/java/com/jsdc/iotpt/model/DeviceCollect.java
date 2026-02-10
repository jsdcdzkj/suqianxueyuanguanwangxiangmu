package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: DeviceCollect
 * @author: wp
 * @description: 采集设备表
 * @date: 2023/5/9 9:34
 */
@Entity
@TableName("DEVICE_COLLECT")
@Table(name = "DEVICE_COLLECT")
@KeySequence(value = "SEQ_DEVICE_COLLECT_ID")
@Data
@ApiModel(value = "采集设备")
public class DeviceCollect implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "deviceCollectId", sequenceName = "SEQ_DEVICE_COLLECT_ID", allocationSize = 1)
    @GeneratedValue(generator = "deviceCollectId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;

    /**
     * 设备型号
     * 关联型号管理（config_model）
     */
    @ApiModelProperty(value = "设备型号")
    private Integer modelNum;

    /**
     * 供应商
     * 关联供应商管理（config_supplier）
     */
    @ApiModelProperty(value = "供应商")
    private Integer supplierId;

    /**
     * 设备编码 序列号
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;




    /**
     * 设备类型
     * 关联设备类型管理表（config_device_type）
     */
    @ApiModelProperty(value = "设备类型")
    private Integer deviceType;

    /**
     * 设备状态 1未使用;  2使用中;  3损坏;  4维修中
     * 【字典 deviceStatus】
     */
    @ApiModelProperty(value = "设备状态")
    private String status;

    /**
     * 所属楼宇
     * 关联楼宇表id
     */
    @ApiModelProperty(value = "所属楼宇")
    private Integer buildId;

    /**
     * 所属楼层
     * 关联楼层表id
     */
    @ApiModelProperty(value = "所属楼层")
    private Integer floorId;

    /**
     * 物理位置
     * 所属区域
     * 关联区域表id
     */
    @ApiModelProperty(value = "物理位置")
    private Integer areaId;

    /**
     * 安装位置
     */
    @ApiModelProperty(value = "安装位置")
    private String place;

    /**
     * 逻辑位置
     * 所属区域
     * 关联区域表id
     */
    @ApiModelProperty(value = "逻辑位置-所属区域")
//    @TableField(jdbcType = JdbcType.NUMERIC, updateStrategy = FieldStrategy.IGNORED)
    private Integer logicalAreaId;
    @ApiModelProperty(value = "逻辑位置-所属楼层")
//    @TableField(jdbcType = JdbcType.NUMERIC, updateStrategy = FieldStrategy.IGNORED)
    private Integer logicalFloorId;
    @ApiModelProperty(value = "逻辑位置-所属楼宇")
    private Integer logicalBuildId;

    /**
     * 逻辑位置
     * 是否分项总设备 0否 1是
     */
    @ApiModelProperty(value = "是否分项总设备")
    private Integer isSubitemTotalDevice;

    /**
     * 逻辑位置
     * 是否总设备 0否 1是
     */
    @ApiModelProperty(value = "是否总设备")
    private Integer isTotalDevice;

    /**
     * 安装日期
     */
    @ApiModelProperty(value = "安装日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date installationDate;

    /**
     * 使用日期
     */
    @ApiModelProperty(value = "使用日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date useTime;

    /**
     * 设备管理员
     * 关联用户表id
     */
    @ApiModelProperty(value = "设备管理员")
    private Integer userId;

    /**
     * 管理员电话
     */
    @ApiModelProperty(value = "管理员电话")
    private String phone;

    /**
     * 维保公司/供应商类型【字典 supplierType】
     */
    @ApiModelProperty(value = "维保公司")
    private String companyId;


    /**
     * 年检日期
     */
    @ApiModelProperty(value = "年检日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inspectionDate;

    /**
     * 采购日期
     */
    @ApiModelProperty(value = "采购日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date procureDate;

    /**
     * 过保日期
     */
    @ApiModelProperty(value = "过保日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expirationDate;

    /**
     * 所属分项
     * 关联设备分项管理（config_device_subitem）
     */
    @ApiModelProperty(value = "所属分项")
    private Integer subitem;

    /**
     * 设备描述
     */
    @ApiModelProperty(value = "设备描述")
    private String deviceDesc;

    /**
     * 网关设备
     * 关联设备所属网关id
     */
    @ApiModelProperty(value = "网关设备")
    private Integer gatewayId;

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

    /**
     * 设备在线状态（1正常、2离线、3报警）
     */
    @ApiModelProperty(value = "设备在线状态")
    private String onLineStatus;

    /**
     * 负荷
     */
    @ApiModelProperty(value = "负荷")
    private String load;



    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String electricVal;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Integer> deviceTypes;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String deviceTypeCode;

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
    private String deviceTypeName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String address;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String location;
    //设备类型的图片
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String filePath;


    //定额值
    private String ratedValue;

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

    @ApiModelProperty(value = "是否租户（1：是，0：否）")
    private Integer isTenant;

    @ApiModelProperty(value = "所属租户/组织")
    private Integer orgId;

    @ApiModelProperty(value = "是否绑定空开（1：是，0：否）")
    private Integer bindSwitch;

    @ApiModelProperty(value = "空开设备id，英文逗号分隔")
    private String switchIds;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<String> switchIdList;

    //设备ids
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String deviceIds;
}
