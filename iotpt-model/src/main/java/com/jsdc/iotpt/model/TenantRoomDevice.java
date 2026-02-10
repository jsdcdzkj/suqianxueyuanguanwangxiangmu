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
 * 租赁房间设备关联表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_ROOM_DEVICE")
@Table(name = "TENANT_ROOM_DEVICE")
@KeySequence(value = "SEQ_TENANT_ROOM_DEVICE_ID")
@Data
public class TenantRoomDevice extends Model<TenantRoomDevice> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_ROOM_DEVICE_ID", sequenceName = "SEQ_TENANT_ROOM_DEVICE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_ROOM_DEVICE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租赁房间ID")
    private Integer tenantRoomId;

    @ApiModelProperty(value = "设备ID")
    private Integer deviceId;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型 1：电表")
    private Integer type;

    @ApiModelProperty(value = "设备初始值")
    private String initialValue;

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
