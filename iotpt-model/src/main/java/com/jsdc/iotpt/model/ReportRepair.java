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
 * ClassName: ReportRepair
 * Description: 上报报修
 * date: 2023/11/22 13:37
 *
 * @author bn
 */
@Entity
@TableName("REPORT_REPAIR")
@Table(name = "REPORT_REPAIR")
@KeySequence(value = "SEQ_REPORT_REPAIR_ID")
@Data
@ApiModel(value = "访客信息表")
public class ReportRepair implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_REPORT_REPAIR_ID", sequenceName = "SEQ_REPORT_REPAIR_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_REPORT_REPAIR_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;


    // 上报类型
    @ApiModelProperty(value = "上报类型")
    private String reportType;

    // 紧急程度
    @ApiModelProperty(value = "紧急程度")
    private String urgency;

    // 关联空间
    @ApiModelProperty(value = "关联空间")
    private Integer areaId;

    // 关联空间类型 0楼宇 1楼层 2区域
    @ApiModelProperty(value = "关联空间类型")
    private Integer areaType;

    // 上报人
    @ApiModelProperty(value = "上报人")
    private Integer reportUser;

    // 上报时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "上报时间")
    private Date reportTime;

    // 上报内容
    @ApiModelProperty(value = "上报内容")
    private String reportRemark;

    // 处理人
    @ApiModelProperty(value = "处理人")
    private Integer handleUser;

    // 处理内容
    @ApiModelProperty(value = "处理内容")
    private String handleRemark;

    // 处理时间
    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    // 处理状态 1.待处理，2，已处理
    @ApiModelProperty(value = "处理状态")
    private Integer handleStatus;


    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;


    /**
     * 更新人id
     */
    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "上传文件")
    public List<SysFile> sysFiles;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


    @Transient
    @TableField(exist = false)
    private String urgencyName;

    @Transient
    @TableField(exist = false)
    private String reportTypeName;

    @Transient
    @TableField(exist = false)
    private String areaName;

}
