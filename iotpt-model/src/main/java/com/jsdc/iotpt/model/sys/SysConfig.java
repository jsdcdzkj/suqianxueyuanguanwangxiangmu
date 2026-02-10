package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 系统配置
 *
 * @author wangYan
 * @since 2023-07-20
 */
@Entity
@TableName("SYS_CONFIG")
@Table(name = "SYS_CONFIG")
@KeySequence(value = "SEQ_SYS_CONFIG_ID")
@Data
@ApiModel(value = "系统配置")
public class SysConfig extends Model<SysConfig> {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_CONFIG_ID", sequenceName = "SEQ_SYS_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_CONFIG_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "系统用户id")
    private Integer id;

    /**
     * 配置名称
     */
    @Comment("配置名称")
    @ApiModelProperty(value = "配置名称")
    private String configName;
    /**
     * 配置值
     */
    @Comment("配置值")
    @ApiModelProperty(value = "配置值")
    private String configValue;
    /**
     * 配置标识
     */
    @Comment("配置标识")
    @ApiModelProperty(value = "配置标识")
    private String configSign;
    /**
     * 设备类型ID
     */
    @Comment("设备类型ID")
    @ApiModelProperty(value = "设备类型ID")
    private Integer deviceTypeId;
    /**
     * 设备ID
     */
    @Comment("设备ID")
    @ApiModelProperty(value = "设备ID")
    private String deviceId;

    /**
     * 描述
     */
    @Comment("描述")
    @ApiModelProperty(value = "描述")
    private String memo;

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

    /**
     * 设备类型ids
     */
    @Transient
    @TableField(exist = false)
    private List deviceIdList;
    /**
     * 设备类型名称
     */
    @Transient
    @TableField(exist = false)
    private String deviceTypeName;

    /**
     * 设备名称
     */
    @Transient
    @TableField(exist = false)
    private String deviceName;
    /**
     * 创建人真实姓名
     */
    @Transient
    @TableField(exist = false)
    private String createUserName;
}
