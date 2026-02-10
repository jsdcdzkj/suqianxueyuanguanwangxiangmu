package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会议室配置
 */
@Entity
@TableName("MEETING_ROOM_CONFIG")
@Table(name = "MEETING_ROOM_CONFIG")
@KeySequence(value = "SEQ_MEETING_ROOM_CONFIG_ID")
@Data
public class MeetingRoomConfig {

    private static final long serialVersionUID = 1L;

    /**
     * 会议室id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "meetingRoomConfigId", sequenceName = "SEQ_MEETING_ROOM_CONFIG_ID", allocationSize = 1)
    @GeneratedValue(generator = "meetingRoomConfigId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 会议室名称
     */
    @ApiModelProperty(value = "会议室名称")
    private String roomName;
    /**
     * 所属楼宇
     */
    @ApiModelProperty(value = "所属楼宇")
    private Integer buildId;
    /**
     * 所属楼宇
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "所属楼宇")
    private String buildName;

    /**
     * 所属楼层
     */
    @ApiModelProperty(value = "所属楼层")
    private Integer floorId;

    /**
     * 所属楼层
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "所属楼层")
    private String floorName;
    /**
     * 会议室所属楼层
     */
    @ApiModelProperty(value = "会议室所属楼层")
    private Integer areaId;
    /**
     * 会议室类型 1:小会议室 2:大会议室 3:多功能会议室
     */
    @ApiModelProperty(value = "会议室类型")
    private Integer roomType;
    /**
     * 容纳人数
     */
    @ApiModelProperty(value = "容纳人数")
    private Integer roomCapacity;
    /**
     * 会议室状态 1:空闲 2:会议中 3: 已预约 4:已结束
     */
    @ApiModelProperty(value = "会议室状态")
    private Integer roomStatus;
    /**
     * 会议室面积 m2
     */
    @ApiModelProperty(value = "会议室面积")
    private String roomAreaSize;
    /**
     * 会议室描述
     */
    @ApiModelProperty(value = "会议室描述")
    private String roomDesc;

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
     * 当前模式/场景  MeetingScene id
     * 为空则 当前无场景
     */
    @ApiModelProperty(value = "当前模式/场景")
    private String currentMode;
    // 会议室编号
    private Integer roomCode;

    /**
     * 联动场景
     * 0：无
     * 1：开启
     * 2: 关闭
     */
    @ApiModelProperty(value = "联动场景")
    private Integer LinkageScenario;

    // 创建人名称
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String createUserName;
    // 关联的预约会议
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<MeetingRoomReservation> meetingRoomReservations;

    //预约时间
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String yyDate;

    //预约情况
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Map<String, Object>> reservationCondition;

    /**
     * 会议开始时间
     */
    @Transient
    @TableField(exist = false)
    private String startTime;

    /**
     * 会议结束时间
     */
    @Transient
    @TableField(exist = false)
    private String endTime;

    /**
     * 可选时间范围
     */
    @Transient
    @TableField(exist = false)
    private String timeRange;

    /**
     * 新增会议时 添加空调设备参数
     */
    @Transient
    @TableField(exist = false)
    private List<Integer> airConditioner;
    /**
     * 新增会议时 添加照明设备参数
     */
    @Transient
    @TableField(exist = false)
    private List<Integer> lighting;
    /**
     * 新增会议时 添加多媒体设备参数
     */
    @Transient
    @TableField(exist = false)
    private Integer multimedia;
    /**
     * 新增会议时 添加感应设设备参数
     */
    @Transient
    @TableField(exist = false)
    private List<Integer> induction;
    /**
     * 新增会议时 添加窗帘参数
     */
    @Transient
    @TableField(exist = false)
    private List<Integer> curtain;
    /**
     * 会议室窗帘数量
     */
    @Transient
    @TableField(exist = false)
    private Long curtainCount;
//    /**
//     * 新增会议时 添加照明设备参数
//     */
//    @Transient
//    @TableField(exist = false)
//    private List<ChintCloudDevicePoint> lightingList;
//    /**
//     * 新增会议时 添加空调设备参数 todo 暂时使用人体感应设备
//     */
//    @Transient
//    @TableField(exist = false)
//    private List<DeviceCollect> airConditionerList;
    /**
     * 会议室的设备类型
     */
    @Transient
    @TableField(exist = false)
    private List<MeetingSceneControl> roomDeviceType;
    /**
     * 会议室数量
     */
    @Transient
    @TableField(exist = false)
    private String roomNum;

    /**
     * 当前模式/场景 名称
     */
    @Transient
    @TableField(exist = false)
    private String currentModeStr;
    @Transient
    @TableField(exist = false)
    private SysFile file;

}
