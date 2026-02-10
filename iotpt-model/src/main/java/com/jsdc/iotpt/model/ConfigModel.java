package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @projectName: IOT
 * @className: ConfigModel
 * @author: wp
 * @description: 设备型号表
 * @date: 2023/5/9 9:25
 */
@Entity
@TableName("CONFIG_MODEL")
@Table(name = "CONFIG_MODEL")
@KeySequence(value = "SEQ_CONFIG_MODEL_ID")
@Data
@ApiModel(value = "设备型号")
public class ConfigModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CONFIG_MODEL_ID", sequenceName = "SEQ_CONFIG_MODEL_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIG_MODEL_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 型号名称
     */
    @ApiModelProperty(value = "型号名称")
    private String modelName;

    /**
     * 所属供应商
     */
    @ApiModelProperty(value = "所属供应商")
    private Integer supplierId;

    /**
     * 型号说明
     */
    @ApiModelProperty(value = "型号说明")
    private String illustrate;

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

    @Transient
    @TableField(exist = false)
    private ConfigSupplier supplier;

    @Transient
    @TableField(exist = false)
    private String supplierName;
}
