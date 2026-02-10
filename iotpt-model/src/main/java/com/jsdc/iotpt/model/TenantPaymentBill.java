package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户充值记录  /退款记录表
 */
@Entity
@TableName("TENANT_PAYMENT_BILL")
@Table(name = "TENANT_PAYMENT_BILL")
@KeySequence(value = "SEQ_TENANT_PAYMENT_BILL_ID")
@Data
public class TenantPaymentBill implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_PAYMENT_BILL_ID", sequenceName = "SEQ_TENANT_PAYMENT_BILL_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_PAYMENT_BILL_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;

    @ApiModelProperty(value = "房间ID")
    private Integer tenantRoomId;

    @ApiModelProperty(value = "金额")
    private String amount;

    @ApiModelProperty(value = "充值方式 字典 tenant_pay_type")
    private Integer payType;

    @ApiModelProperty(value = "费用类型 字典 tenant_expense_type")
    private Integer expenseType;

    @ApiModelProperty(value = "交易类型:充值/退款类型  1：充值，2：退款")
    private Integer tradingType;

    @ApiModelProperty(value = "凭证文件地址")
    private String credentials;

    @ApiModelProperty(value = "交易号")
    private String transNo;

    @ApiModelProperty(value = "交易收据")
    private String transReceipt;

    @ApiModelProperty(value = "创建人")
    private Integer createUser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
