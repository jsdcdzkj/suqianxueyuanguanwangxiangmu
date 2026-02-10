package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("TENANT_INFO")
@Table(name = "TENANT_INFO")
@KeySequence(value = "SEQ_TENANT_INFO_ID")
@Data
public class TenantInfo extends Model<TenantInfo> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TENANT_INFO_ID", sequenceName = "SEQ_TENANT_INFO_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TENANT_INFO_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "租赁房间ID")
    private Integer tenantRoomId;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "联系人")
    private String username;

    @ApiModelProperty(value = "联系方式")
    private String phone;

    @ApiModelProperty(value = "租赁期限-开始 yyyy-MM-dd")
    private String startDate;

    @ApiModelProperty(value = "租赁期限-结束 yyyy-MM-dd")
    private String endDate;

    @ApiModelProperty(value = "租赁合同")
    private String tenantContract;

    @ApiModelProperty(value = "租赁状态 0：租赁中，1：已退租，2：未开始")
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
