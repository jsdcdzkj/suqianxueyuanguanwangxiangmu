package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ClassName: ControlSheet
 * Description: 控制反馈表
 * date: 2023/12/23 9:02
 *
 * @author bn
 */
@Entity
@TableName("CONTROL_SHEET")
@Table(name = "CONTROL_SHEET")
@KeySequence(value = "SEQ_CONTROL_SHEET_ID")
@Data
public class ControlSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "alertSheetId", sequenceName = "SEQ_ALERT_SHEET_ID", allocationSize = 1)
    @GeneratedValue(generator = "alertSheetId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    //网关编码code
    @ApiModelProperty(value = "网关编码")
    public String gateWayId;

    //采集终端设备编码code
    @ApiModelProperty(value = "采集终端设备编码")
    public String deviceId;

    //设备通道
    @ApiModelProperty(value = "设备通道")
    public String channelId;


    //数值
    @ApiModelProperty(value = "数值")
    public String val;

    //控制时间
    public String controlTime;

    // 控制结果
    public String controlResult;

    // 数据来源 主题id
    public Integer topicId;
}
