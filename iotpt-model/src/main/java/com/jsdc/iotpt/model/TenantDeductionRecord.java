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
 * 租户扣费记录
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_DEDUCTION_RECORD")
@Table(name = "TENANT_DEDUCTION_RECORD")
@KeySequence(value = "SEQ_TENANT_DEDUCTION_ID")
@Data
public class TenantDeductionRecord extends Model<TenantDeductionRecord> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_DEDUCTION_ID", sequenceName = "SEQ_TENANT_DEDUCTION_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_DEDUCTION_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租赁房间ID")
    private Integer tenantRoomId;

    @ApiModelProperty(value = "费用类型 字典 tenant_expense_type")
    private Integer expenseType;

    @ApiModelProperty(value = "金额")
    private String amount;

    @ApiModelProperty(value = "扣费前金额")
    private String beforeAmount;

    @ApiModelProperty(value = "扣费后金额")
    private String afterAmount;

    @ApiModelProperty(value = "设备ID")
    private Integer deviceId;

    @ApiModelProperty(value = "用量")
    private String used;

    @ApiModelProperty(value = "用量单位")
    private String unit;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
