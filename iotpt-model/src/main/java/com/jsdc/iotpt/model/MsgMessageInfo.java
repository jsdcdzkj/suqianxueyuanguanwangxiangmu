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
 * ClassName: MsgMessageInfo
 * Description: 消息服务
 * date: 2023/5/8 15:05
 *
 * @author bn
 */
@Entity
@TableName("msg_message_info")
@Table(name = "msg_message_info")
@KeySequence(value = "SEQ_MSG_MESSAGE_INFO_ID")
@Data
@ApiModel(value ="消息服务")
public class MsgMessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "msgMessageInfoId", sequenceName = "SEQ_MSG_MESSAGE_INFO_ID", allocationSize = 1)
    @GeneratedValue(generator = "msgMessageInfoId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="消息服务id")
    private Integer id;//主键
    @ApiModelProperty(value ="消息服务名称")
    private String msgName;//服务名称
    @ApiModelProperty(value ="消息服务类型")
    private String msgType;//服务类型  1邮件; 2 短信; 字典获取
    @ApiModelProperty(value ="状态")
    private Integer status;//状态  0禁用 ; 1启用
    @ApiModelProperty(value ="邮箱类型")
    private String emailType;//邮箱类型  字典获取
    @ApiModelProperty(value ="消息服务协议类型")
    private String protocol;//协议类型 字典获取
    @ApiModelProperty(value ="消息服务服务器地址")
    private String server;//服务器地址
    @ApiModelProperty(value ="消息服务端口号")
    private Integer port;//端口号
    @ApiModelProperty(value ="消息服务用户名")
    private String name;//用户名
    @ApiModelProperty(value ="消息服务密码")
    private String pwd;//密码
    @ApiModelProperty(value ="消息服务签名")
    private String signName;//签名
    @ApiModelProperty(value ="消息服务模板")
    private String templateCode;//模板
    @ApiModelProperty(value ="消息服务访问密钥")
    private String accessKey;//访问密钥
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "发件人")
    private String sender;
    @ApiModelProperty(value = "编号")
    private String regionId;
    @ApiModelProperty(value = "标识")
    private String accessKeyId;
    @ApiModelProperty(value ="密钥")
    private String secret;//密钥
    @ApiModelProperty(value = "开启SSL 1是 0否")
    private String isSSL;// 开启SSL 1是 0否
    @ApiModelProperty(value = "说明")
    private String remarks;// 说明

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


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String msgTypeName;

}
