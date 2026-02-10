package com.jsdc.iotpt.vo.operate;

import lombok.Data;



/**
 * 设备信息
 */
@Data
public class DeviceVo {

    private Integer id;
    //设备名称
    private String name;
    //设备类型 1、采集设备；2、视频设备； 3、网关设备
    private Integer type;
    //设备编码
    private String deviceCode;
    //位置
    private String address;
    //拼接表的id
    private String pId;
}
