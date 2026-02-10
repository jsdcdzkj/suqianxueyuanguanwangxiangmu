package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: WarnSheet
 * Description: 系统告警记录表
 * <pre>记录满足系统配置的告警配置中的数据 每满足一次记录一次数据</pre>
 * date: 2023/7/20 11:36
 *
 * @author bn
 */
@Entity
@TableName("WARN_SHEET")
@Table(name = "WARN_SHEET")
@KeySequence(value = "SEQ_WARN_SHEET_ID")
@Data
public class WarnSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "warnSheetId", sequenceName = "SEQ_WARN_SHEET_ID", allocationSize = 1)
    @GeneratedValue(generator = "warnSheetId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Transient
    @TableField(exist = false)
    private Integer warningInfoId;


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

    private String handleStatus;// 处理状态 字典 warnStatus  1：待响应 2：处理中 3：已处理 4：自动恢复 5：忽略

    private String status;// 告警状态  0：告警中 1.已处理
    private String warnLevel;//告警等级
    private String warnType;//告警类型

    private String warnSource;//告警来源  0告警的异常设备、1上报的异常设备

    private String warnContent;//告警内容
    // 所属分项
    private Integer subitem;

    // 在线状态 ，0恢复，1产生;
    private Integer s;

    @ApiModelProperty(value = "告警类别  1 人员安全 2车辆安全 3厨房安全 4消防安全")
    private String warnCategory;//告警类别  1 人员安全 2车辆安全 3厨房安全 4消防安全
    private String warnDeviceType;//告警设备类型   1采集设备;2NB烟感;3NB紧急按钮;4视频5门禁6电梯
}
