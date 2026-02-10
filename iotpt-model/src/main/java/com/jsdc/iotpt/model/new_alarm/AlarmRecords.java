package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@TableName("ALARM_RECORDS")
@Table(name = "ALARM_RECORDS")
@KeySequence(value = "SEQ_ALARM_RECORDS_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警信息表")
@Accessors(chain = true)
public class AlarmRecords extends Model<AlarmRecords> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_ALARM_RECORDS_ID", sequenceName = "SEQ_ALARM_RECORDS_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ALARM_RECORDS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "告警信息表主键id")
    private Integer id;

    /**
     * 1=摄像头设备
     * 2=采集设备
     * 3=门禁/闸机
     * 4=电梯设备
     * 5=子系统设备
     * 6=租户
     */
    //告警来源
    @ApiModelProperty(value = "告警来源")
    private  Integer deviceSource;

    //设备ID
    @ApiModelProperty(value = "设备ID")
    private  Integer deviceId;

    //设备名称
    @ApiModelProperty(value = "设备名称")
    private  String deviceName;

    //网关ID
    @ApiModelProperty(value = "网关ID")
    private  Integer gatewayId;

    //设备类型ID
    @ApiModelProperty(value = "设备类型ID")
    private  Integer deviceType;

    //楼宇
    @ApiModelProperty(value = "楼宇")
    private  Integer buildId;

    //楼层
    @ApiModelProperty(value = "楼层")
    private  Integer floorId;

    //区域
    @ApiModelProperty(value = "区域")
    private  Integer areaId;

    //告警时间
    @ApiModelProperty(value = "告警时间")
    private  String alarmTime;

    /**
     * 人员告警
     * 车辆告警
     * 厨房告警
     * 电气告警
     * 设备运维告警
     * 消防告警
     * 能耗预警
     * 余额告警
     * 门禁告警
     */
    //告警类型
    @ApiModelProperty(value = "告警类型")
    private  Integer alarmCategory;

    //告警内容ID
    @ApiModelProperty(value = "告警内容ID")
    private  Integer alarmContentId;

    //告警级别
    @ApiModelProperty(value = "告警级别")
    private  Integer alarmLevel;

    //告警内容
    @ApiModelProperty(value = "告警内容")
    private  String alarmContentStr;

    @ApiModelProperty(value = "告警种类，字典：alarmType")
    private Integer alarmType;

    //告警内容
    @ApiModelProperty(value = "告警状态")
    private  String alarmStatus;

    //告警图片
    @ApiModelProperty(value = "告警图片")
    private  String alarmImg;

    //告警视频
    @ApiModelProperty(value = "告警视频")
    private  String alarmVideo;

    /**
     * "0=未处理
     * 1=误报
     * 2=上报
     * 3=忽略"
     */
    //处理状态
    @ApiModelProperty(value = "处理状态")
    private  String handleStatus;

    //处理人
    @ApiModelProperty(value = "处理人")
    private  Integer handleUser;

    //处理时间
    @ApiModelProperty(value = "处理时间")
    private  String handleTime;

    //是否删除
    @ApiModelProperty(value = "是否删除")
    private  Integer isDel;

    //删除操作人
    @ApiModelProperty(value = "删除操作人")
    private  Integer delUser;

    @Transient
    @TableField(exist = false)
    private String areaName;//区域名称

    @Transient
    @TableField(exist = false)
    private String floorName;//楼层名称

    @Transient
    @TableField(exist = false)
    private String buildName;//建筑名称

    @Transient
    @TableField(exist = false)
    private String alarmLevelName;//告警级别名称

    @Transient
    @TableField(exist = false)
    private String alarmCategoryName;//告警类型名称

    @Transient
    @TableField(exist = false)
    private String alarmStatusName;//告警状态名称

    @Transient
    @TableField(exist = false)
    private String handleStatusName;//告警处理名称

    @Transient
    @TableField(exist = false)
    private String alarmCode;//告警编号

    @Transient
    @TableField(exist = false)
    private String deviceSourceName;//报警来源名称

}
