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
import java.util.List;

/**
 * 租赁房间信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_ROOM")
@Table(name = "TENANT_ROOM")
@KeySequence(value = "SEQ_TENANT_ROOM_ID")
@Data
public class TenantRoom extends Model<TenantRoom> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_ROOM_ID", sequenceName = "SEQ_TENANT_ROOM_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_ROOM_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "房间名称")
    private String roomName;

    @ApiModelProperty(value = "楼层ID")
    private Integer floorId;

    @ApiModelProperty(value = "区域ID")
    private Integer areaId;

    @ApiModelProperty(value = "房租单价")
    private String unitRent;

    @ApiModelProperty(value = "场地面积")
    private String areaSize;

    @ApiModelProperty(value = "物业费")
    private String propertyFee;

//    @ApiModelProperty(value = "水费余额")
//    private String waterAmount;

    @ApiModelProperty(value = "电费余额")
    private String electricityAmount;

//    @ApiModelProperty(value = "燃气费余额")
//    private String gasAmount;

    @ApiModelProperty(value = "电表模式 1：预付费模式")
    private Integer electricityMeterType;

    @ApiModelProperty(value = "状态：0：未出租，1：已出租")
    private Integer status;

    @ApiModelProperty(value = "是否启用 ：0：停用，1：启用")
    private Integer isEnable;

    @ApiModelProperty(value = "图纸")
    private String blueprint;

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
