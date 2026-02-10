package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @authon thr
 * @describe 连接管理
 */
@Entity
@TableName("config_link")
@Table(name = "config_link")
@KeySequence(value = "SEQ_CONFIG_LINK_ID")
@Data
@ApiModel(value = "连接管理")
public class ConfigLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configLinkId", sequenceName = "SEQ_CONFIG_LINK_ID", allocationSize = 1)
    @GeneratedValue(generator = "configLinkId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "连接管理id")
    private Integer id;

    //
    @ApiModelProperty(value = "连接名称")
    private String linkName;
    //
    @ApiModelProperty(value = "传输协议")
    private String protocolId;
    //
    @ApiModelProperty(value = "服务地址")
    private String serviceAddress;
    //
    @ApiModelProperty(value = "客户端主键")
    private String clientId;
    //
    @ApiModelProperty(value = "用户名")
    private String username;
    //
    @ApiModelProperty(value = "密码")
    private String password;
    //清除会话 0:否 1:是
    @ApiModelProperty(value = "清除会话")
    private Integer cleanSession;
    //自动重连 0:否 1:是
    @ApiModelProperty(value = "自动重连")
    private Integer reconnect;
    //
    @ApiModelProperty(value = "超时时间")
    private Integer connectTimeOut;
    //
    @ApiModelProperty(value = "心跳间隔")
    private Integer heartbeatTime;
    //
    @ApiModelProperty(value = "连接描述")
    private String linkDesc;

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

    @ApiModelProperty(value = "apiKey")
    private String apiKey;
    @ApiModelProperty(value = "secretKey")
    private String secretKey;

    /**
     * 连接状态 1断开 2连接
     */
    @ApiModelProperty(value = "连接状态")
    private Integer connectionStatus;
}
