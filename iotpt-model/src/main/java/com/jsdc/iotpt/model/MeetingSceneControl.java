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
import java.util.List;

/**
 * 会议室场景控制
 * * @Author：jxl
 * * @Date：2024/6/13 10:19
 * * @FileDesc：
 */
@Entity
@TableName("MEETING_SCENE_CONTROL")
@Table(name = "MEETING_SCENE_CONTROL")
@KeySequence(value = "SEQ_MEETING_SCENE_CONTROL_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSceneControl extends Model<MeetingSceneControl> {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "meetingSceneControlId", sequenceName = "SEQ_MEETING_SCENE_CONTROL_ID", allocationSize = 1)
    @GeneratedValue(generator = "meetingSceneControlId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ApiModelProperty(value = "会议室id")
    private Integer meetingId;

    @ApiModelProperty(value = "设备id")
    private Integer deviceId;

    @ApiModelProperty(value = "设备类型 1：空调 2：照明 3：多媒体 4：人体感应 5:窗帘")
    private Integer deviceType;
    @ApiModelProperty(value = "是否删除 1是0否")
    private Integer isDel;

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
     * 设备详情
     */
    @Transient
    @TableField(exist = false)
    private MeetingSceneDeviceInfo device;

    //新增入参
    @Transient
    @TableField(exist = false)
    private List<Integer> deviceIds;
    /**
     * 新增会议时 添加照明设备参数
     */
    @Transient
    @TableField(exist = false)
    private List<ChintCloudDevicePoint> lightingList;
    /**
     * 新增会议时 添加空调设备参数 todo 暂时使用人体感应设备
     */
    @Transient
    @TableField(exist = false)
    private List<DeviceCollect> airConditionerList;
    /**
     * 新增会议时 添加多媒体设备参数 todo 暂时使用人体感应设备
     */
    @Transient
    @TableField(exist = false)
    private List<DeviceCollect> multimediaList;
    /**
     * 新增会议时 添加感应设设备参数
     */
    @Transient
    @TableField(exist = false)
    private List<DeviceCollect> inductionList;

}
