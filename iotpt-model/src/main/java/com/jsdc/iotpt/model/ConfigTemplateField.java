package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @authon thr
 * @describe 模板数据信息数据key和value管理
 */
@Entity
@TableName("config_template_field")
@Table(name = "config_template_field")
@KeySequence(value = "SEQ_CONFIG_TEMPLATE_FIELD_ID")
@Data
@ApiModel(value = "模板数据信息数据key和value管理")
public class ConfigTemplateField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configTemplateFieldId", sequenceName = "SEQ_CONFIG_TEMPLATE_FIELD_ID", allocationSize = 1)
    @GeneratedValue(generator = "configTemplateFieldId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 外键，关联config_data_transfer.id
     */
    @ApiModelProperty(value = "模板id")
    private Integer modelId;

    /**
     * 类型：1数据 2告警 3心跳
     */
    @ApiModelProperty(value = "模板类型")
    private String type;

    @ApiModelProperty(value = "模板key")
    private String templateKey;

    @ApiModelProperty(value = "告警模板类型value")
    private String alarmVal;
    @ApiModelProperty(value = "心跳模板类型value")
    private String heartVal;
    @ApiModelProperty(value = "数据模板类型value")
    private String dataVal;

    @ApiModelProperty(hidden = true)
    private Integer isDel;
}
