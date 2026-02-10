package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    /**
     * 设备名称(登录网页->智能应用->车位设置-> 设备名称)配置
     */
    private String dev_name;

    /**
     * 设备ip地址 (登录网页->高级设置->基础网络配 置->基础配置->IP地址)配置
     */
    private String ip;
    /**
     * 设备序列号 (不可更改,不可重复,设备唯一)
     */
    private String sn;
}
