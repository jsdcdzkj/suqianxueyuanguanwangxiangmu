package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 会议室场景
 * * @Author：jxl
 * * @Date：2024/6/13 10:25
 * * @FileDesc：
 */
@Entity
@TableName("MEETING_SCENE")
@Table(name = "MEETING_SCENE")
@KeySequence(value = "SEQ_MEETING_SCENE_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingScene extends Model<MeetingScene> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "meetingSceneId", sequenceName = "SEQ_MEETING_SCENE_ID", allocationSize = 1)
    @GeneratedValue(generator = "meetingSceneId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ApiModelProperty(value = "会议室id")
    private Integer meetingId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;
    @ApiModelProperty(value = "是否引用 1是0否")
    private String isReference;

    @ApiModelProperty(value = "场景介绍")
    private String sceneIntroduce;

    @ApiModelProperty(value = "场景模式 1会议 2离开 3讨论 4个性化")
    private Integer sceneType;

    @ApiModelProperty(value = "默认场景 1是 0否")
    private Integer defaultScene;

    @ApiModelProperty(value = "预启动时间")
    private String preBootTime;

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

    @ApiModelProperty(value = "是否删除 1是0否")
    private Integer isDel;

    @ApiModelProperty(value = "空调状态 0开 -1关闭")
    @Transient
    @TableField(exist = false)
    private Integer airStatus;


    @ApiModelProperty(value = "多媒体状态0开 -1关闭")
    @Transient
    @TableField(exist = false)
    private Integer mediaStatus;

    @ApiModelProperty(value = "灯光状态0开 -1关闭")
    @Transient
    @TableField(exist = false)
    private Integer lightStatus;

    @ApiModelProperty(value = "会议发起人id")
    @Transient
    @TableField(exist = false)
    private Integer meetingUser;
    /**
     * 全升-1/全降-2/不做控制-3
     */
    @Transient
    @TableField(exist = false)
    private Integer generalControl;
}
