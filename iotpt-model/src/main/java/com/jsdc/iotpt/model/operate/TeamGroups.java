package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: TeamGroups
 * @author: wp
 * @description: 班组管理
 * @date: 2023/5/8 13:55
 */
@Entity
@TableName("TEAM_GROUPS")
@Table(name = "TEAM_GROUPS")
@KeySequence(value = "SEQ_TEAM_GROUPS_ID")
@Data
@ApiModel(value = "班组管理")
public class TeamGroups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_TEAM_GROUPS_ID", sequenceName = "SEQ_TEAM_GROUPS_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_TEAM_GROUPS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "班组id")
    private Integer id;

    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    private String name;


    /**
     * 班组说明
     */
    @ApiModelProperty(value = "班组说明")
    private String groupDesc;

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
     * 任务权限名称
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String authName;

    /**
     * 人员数量
     */

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer peopleNum;
    /**
     * 组长
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String groupLeader;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<TeamGroupUser> list = new ArrayList<>();

}
