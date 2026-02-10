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
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("CONFIG_TENANT_NOTICE")
@Table(name = "CONFIG_TENANT_NOTICE")
@KeySequence(value = "SEQ_CONFIG_TENANT_NOTICE_ID")
@Data
@Accessors(chain = true)
public class ConfigTenantNotice extends Model<ConfigTenantNotice> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CONFIG_TENANT_NOTICE_ID", sequenceName = "SEQ_CONFIG_TENANT_NOTICE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIG_TENANT_NOTICE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;

    @ApiModelProperty(value = "租户合同到期")
    private String zhContractExpires;//0 开  1 关

    @ApiModelProperty(value = "租户余额不足")
    private String zhInsufficientBalance;//0 开  1 关

    @ApiModelProperty(value = "电量")
    private String zhElectricity;//0 开  1 关

    @ApiModelProperty(value = "物业合同到期")
    private String wyContractExpires;//0 开  1 关

    @ApiModelProperty(value = "租户余额不足")
    private String wyInsufficientBalance;//0 开  1 关

}
