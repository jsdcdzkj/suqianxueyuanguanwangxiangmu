package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 会议室预约
 */
@Entity
@TableName("MEETING_ROOM_RESERVATION")
@Table(name = "MEETING_ROOM_RESERVATION")
@Data
public class MeetingRoomReservation {
    /**
     * 会议室id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    /**
     * 会议室
     */
    private Integer roomId;
    /**
     * 会议主题
     */
    private String meetingTheme;
    /**
     * 预约人
     */
    private Integer reservationPerson;
    /**
     * 会议室状态 1:预约中 2:会议中 3: 已结束
     */
    private Integer roomStatus;
    /**
     * 会议开始时间
     */
    private String startTime;
    /**
     * 会议结束时间
     */
    private String endTime;
    /**
     * 签到开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date signInStartTime;
    /**
     * 签到结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date signInEndTime;
    /**
     * 会议说明
     */
    private String meetingDescription;
    /**
     * 会议室场景
     */
    private String sceneType;

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
     * 短信备注
     */
    @ApiModelProperty(value = "短信备注")
    private String MsgRemark;


    /**
     * 提前准备时间选项
     * 0：会议开始时开始
     * 5：会议开始前5分钟
     * 10：会是开始前10分钟
     */
    @ApiModelProperty(value = "提前准备时间选项")
    private String advanceStartTime;

    // 关联预约的人
    @Transient
    @TableField(exist = false)
    private List<SysUser> userList;

    // 关联预约的人员id
    @Transient
    @TableField(exist = false)
    private String userIds;

    // 预约人名
    @Transient
    @TableField(exist = false)
    private String reservationPersonName;

    // 预约天
    @Transient
    @TableField(exist = false)
    private String reservationDay;


    //参会人员名称列表
    @Transient
    @TableField(exist = false)
    private List<String> shouldCheckedInList;
    //已签到人员名称列表
    @Transient
    @TableField(exist = false)
    private List<String> checkedInList;
    //未签到人员名称列表
    @Transient
    @TableField(exist = false)
    private List<String> notCheckedInList;
    //参会人员id
    @Transient
    @TableField(exist = false)
    private List<Integer> userIdsList;
    //服务类型
    @Transient
    @TableField(exist = false)
    private List<String> serviceTypes;
    //服务类型
    @Transient
    @TableField(exist = false)
    private List<Integer> serviceTypeIds;
    //二维码
    @Transient
    @TableField(exist = false)
    private String qrCode;
    //会议室名称
    @Transient
    @TableField(exist = false)
    private String roomName;
    //会议室信息
    @Transient
    @TableField(exist = false)
    private MeetingRoomConfig meetingRoomConfig;

    //服务类型
    @Transient
    @TableField(exist = false)
    private String serviceOptionIdList;

    //服务类型名称
    @Transient
    @TableField(exist = false)
    private String serviceOptionNames;

    //服务类型名称
    @Transient
    @TableField(exist = false)
    private Integer roomCapacity;

    /**
     * 会议室场景 翻译字段
     */
    @Transient
    @TableField(exist = false)
    private String sceneTypeStr;
}

