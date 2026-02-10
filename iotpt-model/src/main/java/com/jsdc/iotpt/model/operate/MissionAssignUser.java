package com.jsdc.iotpt.model.operate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: mission
 * @author: zln
 * @description: 我的任务—开启处理时，勾选指派的人员
 * @date: 2023/8/23 13:55
 */
@Entity
@TableName("MISSION_ASSIGN_USER")
@Table(name = "MISSION_ASSIGN_USER")
@KeySequence(value = "SEQ_ASSIGN_USER_ID")
@Data
@ApiModel(value = "处理指派人员表")
public class MissionAssignUser implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "assignUserId", sequenceName = "SEQ_ASSIGN_USER_ID", allocationSize = 1)
    @GeneratedValue(generator = "assignUserId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "处理指派人员表ID")
    private Integer id;
    //关联外键id
    @ApiModelProperty(value = "任务id")
    private Integer missionId;
    @ApiModelProperty(value = "指派人id 干活的人")
    private String userId;
    @ApiModelProperty(value = "班组id")
    private Integer groupId;
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<String> users;
}
