package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@TableName("SUBSCRIBETO")
@Table(name = "SUBSCRIBETO")
@KeySequence(value = "SUBSCRIBETO_ID")
@Data
public class Subscribeto extends Model<Subscribeto> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "subscribetoId", sequenceName = "SUBSCRIBETO_ID", allocationSize = 1)
    @GeneratedValue(generator = "subscribetoId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    //ip
    private String receiveIp;

    //端口号
    private String receivePort;

    //订阅类型
    // alarm	报警，各类需要过报警预案的事件
    //state	状态，设备上下线、通道上下线等
    //business	业务通知，各类业务操作相关通知事件
    //perception	感知消息，各类采集信息事件（GPS、MAC、温湿度等）
    private String category;

    //类型列表,没有该字段就是订阅所有，空数组代表不订阅
    //当category是alarm时，为设备或子系统自定义的报警消息，types参考事件类型-报警类型；
    //当category为business时，为业务事件，一般是子系统上报的业务消息，types参考事件类型-业务类型；
    //当category为perception时，为移动设备上报的消息，types参考事件类型-感知类类型；
    //当category为state时，设备状态变化推送消息，无需送
    private String types;

    //回调地址  列：http://192.168.0.58:8080/dVideo/receiveVideoState
    private String url;

    //订阅名称 唯一值
    private String name;


    //订阅的设备
    private String nodeCodes ;

    //备注
    private String remarke;

    //订阅状态 0未订阅  1已订阅
    private String status;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;


    @Transient
    @TableField(exist = false)
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    private Integer pageSize ;


}
