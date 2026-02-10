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
 * @projectName: IOT
 * @className: ConfigSupplier
 * @author: wp
 * @description: 供应商表
 * @date: 2023/5/9 9:01
 */
@Entity
@TableName("CONFIG_SUPPLIER")
@Table(name = "CONFIG_SUPPLIER")
@KeySequence(value = "SEQ_CONFIG_SUPPLIER_ID")
@Data
@ApiModel(value = "供应商")
public class ConfigSupplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CONFIG_SUPPLIER_ID", sequenceName = "SEQ_CONFIG_SUPPLIER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIG_SUPPLIER_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contacts;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String telephone;

    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String illustrate;

    /**
     * 供应商地址
     */
    @ApiModelProperty(value = "供应商地址")
    private String address;

    /**
     * 供应商类型 字典 1：设备供应商 2：运维服务商
     */
    @ApiModelProperty(value = "供应商类型 字典 1：设备供应商 2：运维服务商")
    private String supplierType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;
}
