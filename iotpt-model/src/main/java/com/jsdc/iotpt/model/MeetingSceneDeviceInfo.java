package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 场景和设备绑定信息
 * * @Author：jxl
 * * @Date：2024/6/13 10:52
 * * @FileDesc：
 */
@Entity
@TableName("MEETING_SCENE_DEVICE_INFO")
@Table(name = "MEETING_SCENE_DEVICE_INFO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSceneDeviceInfo extends Model<MeetingSceneDeviceInfo> {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "会议室id")
    private Integer meetingId;

    @ApiModelProperty(value = "场景id")
    private Integer sceneId;

    @ApiModelProperty(value = "模式设置 0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动")
    private Integer modelSetting;

    @ApiModelProperty(value = "风速设置 1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档")
    private Integer windSpeedSetting;

    @ApiModelProperty(value = "温度设置")
    private Integer temperatureSetting;

    @ApiModelProperty(value = "是否开启 1是 0否")
    private Integer isOpen;

    @ApiModelProperty(value = "亮度 %")
    private Integer luminance;

    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty(value = "多媒体中控")
    private String multimediaCenterControl;

    @ApiModelProperty(value = "感应雷达")
    private String inductionRadar;


    @ApiModelProperty(value = "设备类型 1：空调 2：照明 3：多媒体 4：人体感应 5：窗帘")
    private Integer deviceType;
    @ApiModelProperty(value = "是否开启通知规则 1true0false")
    private Boolean isInform;
    @ApiModelProperty(value = "全升-1/全降-2/不做控制-3")
    private Integer generalControl;
    @ApiModelProperty(value = "是否总控设备 1是0否")
    private Integer isGeneralControl;

    @ApiModelProperty(value = "设备id")
    private Integer deviceId;
    //多媒体 设备中控ip
    private String mediaIp;
    //窗帘ip
    private String curtainIp;
    //多媒体 端口号
    private Integer mediaPort;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    @ApiModelProperty(value = "通知人员")
    private String informUserId;
    @Transient
    @TableField(exist = false)
    private List<String> informUserIds;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // 不是用来做默认创建人的 ，用来当个人个性化场景，需要唯一
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    // 不是用来做默认创建人的 ，用来当个人个性化场景，需要唯一
    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    @ApiModelProperty(value = "是否删除 1是0否")
    private Integer isDel;

    /**
     * 设备详情
     */
    @Transient
    @TableField(exist = false)
    private Map<String, Object> device;

    /**
     * 启动状态:1、开启 2、关闭
     */
    @Transient
    @TableField(exist = false)
    private Integer type;
}
