package com.jsdc.iotpt.model;

import com.alibaba.fastjson.annotation.JSONField;
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
 * 设备类型管理（config_device_type）
 * 曹孙翔
 */
@Entity
@TableName("config_device_type")
@Table(name = "config_device_type")
@KeySequence(value = "SEQ_CONFIG_DEVICE_TYPE_ID")
@Data
@ApiModel(value ="设备类型")
public class ConfigDeviceType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configDeviceTypeId", sequenceName = "SEQ_CONFIG_DEVICE_TYPE_ID", allocationSize = 1)
    @GeneratedValue(generator = "configDeviceTypeId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="设备类型id")
    //主键
    private Integer id;
    //设备类型名称
    @ApiModelProperty(value ="设备类型名称")
    private String deviceTypeName;
    //设备类型编码
    @ApiModelProperty(value ="设备类型编码")
    private String deviceTypeCode;
    //文件名称
    @ApiModelProperty(value ="文件名称")
    private String filename;
    //原文件名称
    @ApiModelProperty(value ="原文件名称")
    private String originalFilename;

    //设备类型编码集合
    @Transient
    @TableField(exist = false)
    private List<String> deviceTypeCodes;

    //描述
    @ApiModelProperty(value ="描述")
    private String deviceTypeDesc;

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
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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
     * 设备样品图
     */
    private Integer deviceFile;

    //图片上传功能
    @Transient
    @TableField(exist = false)
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private SysFile file;

    @Transient
    @TableField(exist = false)
    private Integer online;

    @Transient
    @TableField(exist = false)
    private Integer down;

    @Transient
    @TableField(exist = false)
    private Integer all;
}
