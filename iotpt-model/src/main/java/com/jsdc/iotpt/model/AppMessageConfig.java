package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * AppMessageConfig
 * APP消息配置
 *
 * @author 许森森
 * @date 2024/11/18
 */
@Entity
@TableName("APP_MESSAGE_CONFIG")
@Table(name = "APP_MESSAGE_CONFIG")
@KeySequence(value = "APP_MESSAGE_CONFIG_ID")
@Data
public class AppMessageConfig extends Model<AppMessageConfig> implements Serializable {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "APP_MESSAGE_CONFIG_ID", sequenceName = "APP_MESSAGE_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "APP_MESSAGE_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "配置类型")
    private String type;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "配置类型名称")
    private String typeName;

    @ApiModelProperty(value = "模板Code")
    private String templateCode;

    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "跳转类型")
    private String jumpType;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;

    @ApiModelProperty(value = "是否启用 0:未启用  1：启用")
    private Integer isEnable;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "是否有动态字段（Y/N）")
    private String hasDynamicVal;

    @ApiModelProperty(value = "动态值JSON")
    private String dynamicVal;

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

}
