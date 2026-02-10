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
 * @authon thr
 * @describe 协议管理
 */
@Entity
@TableName("config_protocol")
@Table(name = "config_protocol")
@KeySequence(value = "SEQ_CONFIG_PROTOCOL_ID")
@Data
@ApiModel(value = "协议管理")
public class ConfigProtocol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configProtocolId", sequenceName = "SEQ_CONFIG_PROTOCOL_ID", allocationSize = 1)
    @GeneratedValue(generator = "configProtocolId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "协议管理id")
    private Integer id;
    //
    @ApiModelProperty(value = "协议名称")
    private String protocolName;
    //
    @ApiModelProperty(value = "协议说明")
    private String protocolDesc;

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
