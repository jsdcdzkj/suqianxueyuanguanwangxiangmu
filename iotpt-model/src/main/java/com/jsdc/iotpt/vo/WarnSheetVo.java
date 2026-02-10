package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsdc.iotpt.model.WarnSheet;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
public class WarnSheetVo extends WarnSheet implements Serializable {

    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer value;

    private String name;

    //告警数量
    private String warnNum;

    //采集设备名称
    private String collectName;

    //采集设备区域名称
    private String areaName;

    private String alertTimeStart;
    private String alertTimeEnd;

    // 告警等级名
    private String warnLevelName;


    // 处理状态名称
    private String statusName;

    // 设备名称
    private String deviceName;
    // 告警类型 安防消防设备
    private String warnTypes;
}
