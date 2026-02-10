package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 租赁房间用电配置
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_ELECTRICITY_CONFIG")
@Table(name = "TENANT_ELECTRICITY_CONFIG")
@KeySequence(value = "SEQ_TENANT_ELECTR_CONFIG_ID")
@Data
public class TenantElectricityConfig extends Model<TenantElectricityConfig> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_ELECTR_CONFIG_ID", sequenceName = "SEQ_TENANT_ELECTR_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_ELECTR_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;

    @ApiModelProperty(value = "租赁房间ID")
    private Integer tenantRoomId;

//    @ApiModelProperty(value = "电表初始值")
//    private String electricMeterVal;

    @ApiModelProperty(value = "是否启用峰谷平电价（Y/N）")
    private String peakValley;

    @ApiModelProperty(value = "峰时电价")
    private String peakPrice;

    @ApiModelProperty(value = "峰时电价时间段")
    private String peakTimePeriod;// 08:00-21:00

    @ApiModelProperty(value = "谷时电价")
    private String valleyPrice;

    @ApiModelProperty(value = "谷时电价时间段")
    private String valleyTimePeriod;// 00:00-08:00,21:00-24:00

    @ApiModelProperty(value = "基础电价")
    private String basicPrice;

    @ApiModelProperty(value = "告警金额")
    private String warningAmount;

    @ApiModelProperty(value = "允许赊欠金额")
    private String creditAmount;

    @ApiModelProperty(value = "删除标志")
    private Integer isDel;

    @ApiModelProperty(value = "创建人")
    private Integer createUser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private Integer updateUser;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
