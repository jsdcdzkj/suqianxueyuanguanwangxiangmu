package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigDataTransfer;
import com.jsdc.iotpt.model.ConfigSignalField;
import com.jsdc.iotpt.model.ConfigTemplateField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @authon thr
 * @describe 数据转换模板管理
 */
@Data
public class ConfigDataTransferVo extends ConfigDataTransfer {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    //模板类型名称
    private String modelTypeName;

    //主题管理id
    private Integer configTopicId;

    //数据模板类型 对应字段
    private List<ConfigSignalField> dataList = new ArrayList<>();
    //告警模板类型 对应字段
    private List<ConfigSignalField> alarmList = new ArrayList<>();
    //心跳模板类型 对应字段
    private List<ConfigSignalField> heartList = new ArrayList<>();
    //控制模板类型 对应字段
    private List<ConfigSignalField> controlList=new ArrayList<>();

    //数据模板类型 key和value
    private ConfigTemplateField templateField1;
    //告警模板类型 key和value
    private ConfigTemplateField templateField2;
    //心跳模板类型 key和value
    private ConfigTemplateField templateField3;
    //控制模板类型 key和value
    private ConfigTemplateField templateField4;

    //json数据
    private String jsonData;
    //数据类型 1数据 2告警 3心跳4.控制
    private String dataType;

}
