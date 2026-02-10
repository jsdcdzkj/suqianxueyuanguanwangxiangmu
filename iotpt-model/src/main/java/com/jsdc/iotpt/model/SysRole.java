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
 * @projectName: IOT
 * @className: SysRole
 * @author: wp
 * @description: 角色表
 * @date: 2023/5/8 16:46
 */

@Entity
@TableName("SYS_ROLE")
@Table(name = "SYS_ROLE")
@KeySequence(value = "SEQ_SYS_ROLE_ID")
@Data
@ApiModel(value = "角色")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_ROLE_ID", sequenceName = "SEQ_SYS_ROLE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_ROLE_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 角色名
     */
    @ApiModelProperty(value = "角色名")
    private String roleName;

    /**
     * 角色标志
     */
    @ApiModelProperty(value = "角色标志")
    private String roleFlag;

    //出门条权限标识
    private String roleSymbol;



    /**
     * 所属系统
     */
    @ApiModelProperty(value = "所属系统")
    private Integer systemId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
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
    private List<String> menus;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Integer> menuIds;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String createUserName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String updateUserName;
}
