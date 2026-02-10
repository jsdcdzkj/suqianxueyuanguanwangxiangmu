package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: AlertSheet
 * Description: 设备告警记录表
 * date: 2023/5/19 11:48
 *
 * @author bn
 */
@Entity
@TableName("ALERT_SHEET")
@Table(name = "ALERT_SHEET")
@KeySequence(value = "SEQ_ALERT_SHEET_ID")
@Data
public class AlertSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "alertSheetId", sequenceName = "SEQ_ALERT_SHEET_ID", allocationSize = 1)
    @GeneratedValue(generator = "alertSheetId", strategy = GenerationType.SEQUENCE)
    private Integer id;


    //网关ID sn
    public String gateWayId;
    //设备类型
    public Integer type;
    //设备ID sn
    public String deviceId;

    // 信号id
    public String signalId;

    public String point;
    //告警时间
    public String alertTime;
    //数值
    public String val;

    //告警恢复
    public String alertRecover;

    // 数据来源 主题id
    public Integer topicId;


    public Integer warnConfigId;

    //////补充数据
    /**
     * 1楼宇   楼宇表中取
     * 2楼层   楼层表中取
     * 3位置   采集设备中取
     * 4是否触发告警  告警规则是否匹配
     * 5告警id        匹配告警id
     * 6告警规则      匹配规则
     * 7设备相关信息  设备表中取
     * 8网关相关信息  网关表中取
     */
    public Integer buildId;
    //public String buildName;

    public Integer floorId;
    //public String floorName;

    public Integer areaId;

    public Integer deviceGatewayId; //网关id
    public Integer deviceCollectId; //设备id
    public String isWarn; //4是否触发告警  告警规则是否匹配 T or F


    // 所属分项
    private Integer subitem;
    // 在线状态 ，0恢复，1产生;
    private Integer s;
}
