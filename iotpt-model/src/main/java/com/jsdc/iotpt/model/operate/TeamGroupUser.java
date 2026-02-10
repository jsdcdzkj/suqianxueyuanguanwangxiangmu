package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @projectName: IOT
 * @className: TeamGroupUser
 * @author: wp
 * @description: 班组人员关联表
 * @date: 2023/5/8 13:55
 */
@Entity
@TableName("TEAM_GROUP_USER")
@Table(name = "TEAM_GROUP_USER")
@KeySequence(value = "SEQ_TEAM_GROUP_USER_ID")
@Data
@ApiModel(value = "班组人员关联表")
public class TeamGroupUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TEAM_GROUP_USER_ID", sequenceName = "SEQ_TEAM_GROUP_USER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TEAM_GROUP_USER_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "班组人员表id")
    private Integer id;


    @ApiModelProperty(value = "班组id")
    private Integer groupId;

    @ApiModelProperty(value = "人员Id")
    private Integer userId;

    /**
     * 班组角色 （字典表获取） 1组长 2组员
     */
    @ApiModelProperty(value = "班组角色")
    private String teamRole;

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

    /**
     * 团队角色名称
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String teamRoleName;
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String teamGroupName;

    /**
     * 人员名称
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String realName;


    /**
     * 组织名称
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String orgName;


    /**
     * 部门名称
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String deptName;

    /**
     * 联系电话
     */

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String phone;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String roleNamesStr;

}
