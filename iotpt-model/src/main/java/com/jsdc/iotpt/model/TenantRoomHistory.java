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
 * 房间历史租赁信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_ROOM_HISTORY")
@Table(name = "TENANT_ROOM_HISTORY")
@KeySequence(value = "SEQ_TENANT_ROOM_HISTORY_ID")
@Data
public class TenantRoomHistory extends Model<TenantRoomHistory> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_ROOM_HISTORY_ID", sequenceName = "SEQ_TENANT_ROOM_HISTORY_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_ROOM_HISTORY_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "房间ID")
    private Integer roomId;

    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "联系人")
    private String username;

    @ApiModelProperty(value = "联系方式")
    private String phone;

    @ApiModelProperty(value = "租赁期限-开始 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "租赁期限-结束 yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "租赁合同")
    private String tenantContract;

    @ApiModelProperty(value = "租赁状态 0：租赁中，1：已到期，2：未开始")
    private Integer status;

    @ApiModelProperty(value = "合同金额")
    private String contractAmount;

    @ApiModelProperty(value = "用电总支出")
    private String eTotalAmount;

}
