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


@Entity
@TableName("REPORT_TEMPLATE")
@Table(name = "REPORT_TEMPLATE")
@KeySequence(value = "SEQ_REPORT_TEMPLATE_ID")
@Data
@ApiModel(value = "报告模版")
public class ReportTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_REPORT_TEMPLATE_ID", sequenceName = "SEQ_REPORT_TEMPLATE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_REPORT_TEMPLATE_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ApiModelProperty(value = "模版名称")
    private String tempName;

    @ApiModelProperty(value = "模版类型 1、单 2、双")
    private Integer tempType;

    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private String createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private String updateUser;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "所勾选的报告模块")
    private List<String> reportTypeIds;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "模版类型名称")
    private String tempTypeName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "创建用户名称")
    private String createUserName;
}
