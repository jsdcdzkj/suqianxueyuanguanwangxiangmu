package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备信号关联表（config_device_signal_map）
 * 曹孙翔
 */
@Entity
@TableName("config_device_signal_map")
@Table(name = "config_device_signal_map")
@KeySequence(value = "SEQ_CONFIG_LINK_ID")
@Data
public class ConfigDeviceSignalMap implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configDeviceSignalMapId", sequenceName = "SEQ_CONFIG_LINK_ID", allocationSize = 1)
    @GeneratedValue(generator = "configDeviceSignalMapId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="设备信号关联id")
    //主键
    private Integer id;
    //设备类型id
    @ApiModelProperty(value ="设备类型id")
    private Integer deviceTypeId;
    //信号类型id
    @ApiModelProperty(value ="信号类型id")
    private Integer signalTypeId;

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
}
