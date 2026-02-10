package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: DataSheet
 * Description: 设备采集数据
 * date: 2023/5/19 11:48
 *
 * @author bn
 */
@Entity
@TableName("DATA_SHEET")
@Table(name = "DATA_SHEET")
@KeySequence(value = "SEQ_DATA_SHEET_ID")
@Data
public class DataSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "dataSheetId", sequenceName = "SEQ_DATA_SHEET_ID", allocationSize = 1)
    @GeneratedValue(generator = "dataSheetId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    //网关编码code
    @ApiModelProperty(value = "网关编码")
    public String gateWayId;
    //设备类型
    @ApiModelProperty(value = "设备类型")
    public String type;
    //采集终端设备编码code
    @ApiModelProperty(value = "采集终端设备编码")
    public String deviceId;
    //设备通道
    @ApiModelProperty(value = "设备通道")
    public String channelId;

    // 数据来源 主题id
    public Integer topicId;

    public String point;
    //采集时间
    @ApiModelProperty(value = "采集时间")
    public String dataTime;
    //数值
    @ApiModelProperty(value = "数值")
    public String val;

    //信号类型名称
    @Transient
    @TableField(exist = false)
    private String signalTypeName;



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

    public String place; //3位置   采集设备中取

    // 所属分项
    private Integer subitem;

    private Date time;

    public String isWarn; //4是否触发告警  告警规则是否匹配 T or F

    public Integer warnId;
    public Integer deviceCollectId; //设备id
    public Integer deviceGatewayId; //网关id
}
