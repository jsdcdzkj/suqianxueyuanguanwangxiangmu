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
 * 租户告警信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_WARNING_INFO")
@Table(name = "TENANT_WARNING_INFO")
@KeySequence(value = "SEQ_TENANT_WARNING_INFO_ID")
@Data
public class TenantWarningInfo extends Model<TenantWarningInfo> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_WARNING_INFO_ID", sequenceName = "SEQ_TENANT_WARNING_INFO_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_WARNING_INFO_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;

    @ApiModelProperty(value = "租赁房间ID")
    private Integer tenantRoomId;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "费用类型 字典 tenant_expense_type")
    private Integer expenseType;

    @ApiModelProperty(value = "告警内容")
    private String warnContent;

    @ApiModelProperty(value = "告警状态：0：告警中，1：已处理，2：忽略")
    private Integer status;

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
