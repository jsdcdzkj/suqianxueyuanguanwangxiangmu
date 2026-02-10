package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

/**
 * 泰杰赛智慧照明系统 网关设备
 */

@Data
public class GatewayDto {

    /**
     * 网关设备id
     */
    private String id;

    /**
     * 网关设备标识
     */
    private String deviceKey;

    /**
     * 网关设备名称
     */
    private String deviceName;

    /**
     * 网关设备所用的产品标识
     */
    private String productKey;

    /**
     * 是否是子设备 1 是，0-不是
     */
    private Integer isSubDevice;

    /**
     * 设备的产品通讯类型
     */
    private Integer commType;

    /**
     * 关联节点的类型
     */
    private Integer linkNodeType;

}
