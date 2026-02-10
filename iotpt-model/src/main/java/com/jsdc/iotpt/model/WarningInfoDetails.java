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
 * @description: 告警信息处理表
 * @date: 2023/5/8 17:55
 */
@Entity
@TableName("WARNING_INFO_DETAILS")
@Table(name = "WARNING_INFO_DETAILS")
@KeySequence(value = "SEQ_WARNING_INFO_DETAILS_ID")
@Data
@ApiModel(value = "告警信息明细表")
public class WarningInfoDetails implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WARNING_INFO_DETAILS_ID", sequenceName = "SEQ_WARNING_INFO_DETAILS_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WARNING_INFO_DETAILS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "告警信息明细表主键id")
    private Integer id;


    @ApiModelProperty(value = "告警信息外键ID")
    private Integer warnInfoId;

    @ApiModelProperty(value = "告警等级")
    private String warnLevel;
    @ApiModelProperty(value = "告警设备id")
    private Integer deviceId;
    @ApiModelProperty(value = "告警区域")
    private Integer areaId;
    @ApiModelProperty(value = "告警内容")
    private String content;
    @ApiModelProperty(value = "告警时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date warnTime;
    @ApiModelProperty(value = "告警来源")
    private String warnSource;

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
