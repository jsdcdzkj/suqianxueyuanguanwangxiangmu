package com.jsdc.iotpt.model.operate;

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
 * @projectName: IOT
 * @className: MissionCare
 * @author: zln
 * @description: 我的任务—任务关注表
 * @date: 2023/8/23 13:55
 */
@Entity
@TableName("MISSION_CARE")
@Table(name = "MISSION_CARE")
@KeySequence(value = "SEQ_MISSION_CARE_ID")
@Data
@ApiModel(value = "任务关注表")
public class MissionCare implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "missionCareId", sequenceName = "SEQ_MISSION_CARE_ID", allocationSize = 1)
    @GeneratedValue(generator = "missionCareId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "关注id")
    private Integer id;
    @ApiModelProperty(value = "任务id", required = true)
    private Integer missionId;
    @ApiModelProperty(value = "关注时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "关注人id")
    private Integer createUser;
    @ApiModelProperty(value = "关注类型 1：我的任务关注  2：待指派关注")
    private Integer careType;
}
