package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.DeviceGateway;
import lombok.Data;

@Data
public class DeviceGatewayVo extends DeviceGateway {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    // 区域名
    private String areaName;
    // 供应商名
    private String supplierName;
    // 楼宇名
    private String buildName;
    // 设备型号名
    private String modelName;
    // 楼层名
    private String floorName;
    // 发布主题名
    private String topicNameP;
    // 订阅主题名
    private String topicNameS;
    // 传输协议名
    private String protocolName;
    // 服务连接名
    private String linkName;
    // 转换模板名
    private String transferName;

    //所在区域名称
    private String areaNames;






}
