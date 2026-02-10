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
 * @describe 信号字段管理
 */
@Entity
@TableName("config_signal_field")
@Table(name = "config_signal_field")
@KeySequence(value = "SEQ_CONFIG_SIGNAL_FIELD_ID")
@Data
@ApiModel(value = "模板解析规则配置对应字段管理")
public class ConfigSignalField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configSignalFieldId", sequenceName = "SEQ_CONFIG_SIGNAL_FIELD_ID", allocationSize = 1)
    @GeneratedValue(generator = "configSignalFieldId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 外键，关联config_data_transfer.id
     */
    @ApiModelProperty(value = "模板id")
    private Integer modelId;

    /**
     * 类型：1数据 2告警 3心跳
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 解析对应字段
     */
    @ApiModelProperty(value = "解析对应字段")
    private String gatewayKey;

    /**
     * 系统对应字段
     */
    @ApiModelProperty(value = "系统对应字段")
    private String systemKey;

    @ApiModelProperty(hidden = true)
    private Integer isDel;
}
