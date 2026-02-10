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
 * @author lb
 * @description: 告警配置明细表
 * @date: 2023/5/8 17:55
 */
@Entity
@TableName("WARNING_SIGN_DETAILS")
@Table(name = "WARNING_SIGN_DETAILS")
@KeySequence(value = "SEQ_WARNING_SIGN_DETAILS_ID")
@Data
@ApiModel(value = "告警配置明细表")
public class WarningSignDetails implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WARNING_SIGN_DETAILS_ID", sequenceName = "SEQ_WARNING_SIGN_DETAILS_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WARNING_SIGN_DETAILS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "告警配置明细表主键id")
    private Integer id;

    @ApiModelProperty(value = "配置表外键ID")
    private Integer configId;

    @ApiModelProperty(value = "信号类型")
    private String signType;
    @ApiModelProperty(value = "信号名称")
    private String signName;
    @ApiModelProperty(value = "值类型")
    private String valueType;

    //如果阈numberBool 0是范围值  1是布尔值
    @ApiModelProperty(value = "阈值范围")
    private Integer numberBool;
    @ApiModelProperty(value = "布尔值")
    private Integer valueBoolen;
    @ApiModelProperty(value = "开始范围")
    private double valueBegin;
    @ApiModelProperty(value = "结束范围")
    private double valueEnd;
    @ApiModelProperty(value = "告警级别")
    private String warnLevel;
    @ApiModelProperty(value = "联动场景ID")
    private Integer linkageId;
    @ApiModelProperty(value = "是否启用")
    private Integer isEnable;

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
