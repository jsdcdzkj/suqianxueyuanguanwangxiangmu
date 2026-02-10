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
 * @author lb
 * @description: 告警配置表
 * @date: 2023/5/8 17:55
 */
@Entity
@TableName("WARNING_CONFIG")
@Table(name = "WARNING_CONFIG")
@KeySequence(value = "SEQ_WARNING_CONFIG_ID")
@Data
@ApiModel(value = "告警配置表")
public class WarningConfig implements Serializable {
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WARNING_CONFIG_ID", sequenceName = "SEQ_WARNING_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WARNING_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "配置名称主键id")
    private Integer id;

    @ApiModelProperty(value = "配置名称")
    private String configName;

    @ApiModelProperty(value = "配置范围")
    private String configType;
    @ApiModelProperty(value = "告警类型")
    private String warnType;
    @ApiModelProperty(value = "所属分组")
    private String subset;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;
    @ApiModelProperty(value = "楼宇")
    private Integer buildId;
    @ApiModelProperty(value = "楼层")
    private Integer floor;
    @ApiModelProperty(value = "设备ids")
    private Integer deviceId;//这个是设备的id 不是sn码

    @ApiModelProperty(value = "描述")
    private String memo;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

}
