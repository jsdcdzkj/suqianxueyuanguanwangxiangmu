package com.jsdc.iotpt.model.new_alarm;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("FALSE_ALARM_RECORDS")
@Table(name = "FALSE_ALARM_RECORDS")
@KeySequence(value = "FALSE_ALARM_RECORDS_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "误报信息表")
@Accessors(chain = true)
public class FalseAlarmRecords extends Model<FalseAlarmRecords> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "FALSE_ALARM_RECORDS_ID", sequenceName = "FALSE_ALARM_RECORDS_ID", allocationSize = 1)
    @GeneratedValue(generator = "FALSE_ALARM_RECORDS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "误报信息表主键id")
    private Integer id;


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
     * "人员告警
     * 厨房告警
     * 车辆告警
     * 设备运维告警
     * 消防告警
     * 电气告警
     * 门禁告警
     * 能耗预警"
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

    //告警图片
    @ApiModelProperty(value = "告警图片")
    private  String alarmImg;

    //告警视频
    @ApiModelProperty(value = "告警视频")
    private  String alarmVideo;

    //是否删除
    @ApiModelProperty(value = "是否删除")
    private  Integer isDel;

    @ApiModelProperty(value = "告警种类，字典：alarmType")
    private Integer alarmType;

    //人工判定
    /**
     * 1:误识
     * 2:图像质量不佳
     * 3:环境影响
     * 4:设备原因
     */
    @ApiModelProperty(value = "人工判定")
    private  Integer judge;


    public FalseAlarmRecords(AlarmRecords records,String judge){
        this.setAlarmCategory(records.getAlarmCategory()).setAlarmImg(records.getAlarmImg()).setAlarmContentStr(records.getAlarmContentStr()).setAlarmLevel(records.getAlarmLevel())
                .setAlarmContentId(records.getAlarmContentId()).setAlarmTime(records.getAlarmTime()).setAlarmVideo(records.getAlarmVideo()).setDeviceType(records.getDeviceType())
                .setBuildId(records.getBuildId()).setDeviceName(records.getDeviceName()).setDeviceId(records.getDeviceId()).setDeviceSource(records.getDeviceSource())
                .setFloorId(records.getFloorId()).setAreaId(records.getAreaId()).setIsDel(0).setGatewayId(records.getGatewayId()).setAlarmType(records.getAlarmType());
        this.judge=Integer.parseInt(judge);
    }
}
