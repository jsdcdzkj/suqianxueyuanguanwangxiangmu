package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;

/**
 * SysOperateLog
 *
 * @author 许森森
 * @date 2024/10/21
 */
@Entity
@TableName("SYS_OPERATE_LOG")
@javax.persistence.Table(name = "SYS_OPERATE_LOG")
@org.hibernate.annotations.Table(appliesTo = "SYS_OPERATE_LOG", comment = "操作日志")
@KeySequence(value = "SYS_OPERATE_LOG_ID")
@Data
@ApiModel(value = "操作日志")
public class SysOperateLog {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_OPERATE_LOG_ID", sequenceName = "SEQ_SYS_OPERATE_LOG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_OPERATE_LOG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @Comment("模块")
    @ApiModelProperty(value = "模块")
    private String model;

    @Comment("请求方式")
    @ApiModelProperty(value = "请求方式")
    private String method;

    @Comment("请求接口地址")
    @ApiModelProperty(value = "请求接口地址")
    private String api;

    @Comment("客户端IP地址")
    @ApiModelProperty(value = "客户端IP地址")
    private String requestIp;

    @Comment("请求参数")
    @ApiModelProperty(value = "请求参数")
    private String requestBody;

    @Comment("相应参数")
    @ApiModelProperty(value = "相应参数")
    private String responseBody;

    @Comment("操作人")
    @ApiModelProperty(value = "操作人")
    private String operator;

    @Comment("操作时间")
    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;
}
