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
 * @authon thr
 * @describe 数据转换模板管理
 */
@Entity
@TableName("config_data_transfer")
@Table(name = "config_data_transfer")
@KeySequence(value = "SEQ_CONFIG_DATA_TRANSFER_ID")
@Data
@ApiModel(value = "数据转换模板")
public class ConfigDataTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configDataTransferId", sequenceName = "SEQ_CONFIG_DATA_TRANSFER_ID", allocationSize = 1)
    @GeneratedValue(generator = "configDataTransferId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "数据转换模板id")
    private Integer id;

    //
    @ApiModelProperty(value = "模板名称")
    private String modelName;
    //
    @ApiModelProperty(value = "模板编码")
    private String modelCode;
    //1订阅主题模板 2发布主题模板 3系统对接模板
    @ApiModelProperty(value = "模板类型")
    private String modelType;
    //
    @ApiModelProperty(value = "聚合标识")
    private String polymerization;
    //
    @ApiModelProperty(value = "模板描述")
    private String modelDesc;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

}
