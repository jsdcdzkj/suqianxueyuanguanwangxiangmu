package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: ConfigSignalType
 * @author: wp
 * @description: 信号类型表
 * @date: 2023/5/8 17:04
 */
@Entity
@TableName("CONFIG_SIGNAL_TYPE")
@Table(name = "CONFIG_SIGNAL_TYPE")
@KeySequence(value = "SEQ_CONFIG_SIGNAL_TYPE_ID")
@Data
@ApiModel(value = "信号类型")
public class ConfigSignalType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CONFIG_SIGNAL_TYPE_ID", sequenceName = "SEQ_CONFIG_SIGNAL_TYPE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIG_SIGNAL_TYPE_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 信号类型名称
     */
    @ApiModelProperty(value = "信号类型名称")
    private String signalTypeName;

    /**
     * 信号类型编码
     */
    @ApiModelProperty(value = "信号类型编码")
    private String signalTypeCode;

    /**
     * 统计类型
     * 字典设置: 1：实时值 2：差值 3：累加值
     */
    @ApiModelProperty(value = "统计类型")
    private String statisticalType;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String signalTypeDesc;



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
    @ApiModelProperty(hidden = true)
    private String val;

    /**
     * 信号分类
     * 字典
     */
    @ApiModelProperty(value = "信号类型")
    private String signalType;

    @Transient
    @TableField(exist = false)
    private List<ConfigSignalTypeItem> items;

    /**
     * 数据类型
     * 字典设置: 1.整型 2.小数 3.枚举 4.布尔型
     */
    @ApiModelProperty(value = "数据类型")
    private String dataType;
}
