package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统日志
 *
 * @author wangYan
 * @since 2023-05-08
 */
@Entity
@TableName("SYS_LOG")
@javax.persistence.Table(name = "SYS_LOG")
@org.hibernate.annotations.Table(appliesTo = "SYS_LOG", comment = "系统日志")
@KeySequence(value = "SEQ_SYS_LOG_ID")
@Data
@ApiModel(value = "系统日志")
public class SysLog {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_LOG_ID", sequenceName = "SEQ_SYS_LOG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_LOG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "系统用户id")
    private Integer id;
    /**
     * 日志类别
     */
    @Comment("日志类别")
    @ApiModelProperty(value = "日志类别")
    private String logType;

    /**
     * 模块
     */
    @Comment("模块")
    @ApiModelProperty(value = "模块")
    private String model;


    /**
     * 日志内容
     */
    @Comment("日志内容")
    @ApiModelProperty(value = "日志内容")
    private String content;

    /**
     * 日志内容
     */
    @Comment("日志内容1")
    @ApiModelProperty(value = "日志内容1")
    private String content1;

    @Comment("请求接口地址")
    @ApiModelProperty(value = "请求接口地址")
    private String api;

    @Comment("客户端IP地址")
    @ApiModelProperty(value = "客户端IP地址")
    private String requestAddr;

    @Comment("IP归属地")
    @ApiModelProperty(value = "IP归属地")
    private String ipGeolocation;

    @Comment("请求参数")
    @ApiModelProperty(value = "请求参数")
    private String requestBody;

    @Comment("相应参数")
    @ApiModelProperty(value = "相应参数")
    private String responseBody;

    /**
     * 操作人
     */
    @Comment("操作人")
    @ApiModelProperty(value = "操作人")
    private Integer operator;
    /**
     * 操作时间
     */
    @Comment("操作时间")
    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;

    @Comment("创建人")
    private Integer createUser;
    @Comment("更新人")
    private Integer updateUser;
    @Comment("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Comment("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @Comment("是否删除 0:正常  1：删除")
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "操作人")
    private String operatorName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String startTime;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String endTime;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty("日志类型名称")
    private String logTypeName;
}
