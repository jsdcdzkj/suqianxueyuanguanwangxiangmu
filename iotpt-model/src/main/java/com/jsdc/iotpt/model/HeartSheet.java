package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: AlertSheet
 * Description: 设备心跳数据
 * date: 2023/5/19 11:48
 *
 * @author bn
 */
@Entity
@TableName("HEART_SHEET")
@Table(name = "HEART_SHEET")
@KeySequence(value = "SEQ_HEART_SHEET_ID")
@Data
public class HeartSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "heartSheetId", sequenceName = "SEQ_HEART_SHEET_ID", allocationSize = 1)
    @GeneratedValue(generator = "heartSheetId", strategy = GenerationType.SEQUENCE)
    private Integer id;


    //网关ID
    public String gateWayId;
    //设备ID
    public String deviceId;
    //设备通道
    public String channelId;

    // 心跳状态
    public String heartState;

    public String point;
    //采集时间
    public String heartTime;
    //数值
    public String val;

    // 数据来源 主题id
    public Integer topicId;

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




    public Integer deviceCollectId; //设备id
    public Integer deviceGatewayId; //网关id
}
