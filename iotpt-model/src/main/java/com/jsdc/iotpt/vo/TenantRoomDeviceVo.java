package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantRoomDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class TenantRoomDeviceVo extends TenantRoomDevice {

    private String name;

    private Integer buildId;

    private Integer floorId;

    private Integer areaId;

    private Integer logicalBuildId;

    private Integer logicalFloorId;

    private Integer logicalAreaId;

    private String status;

    private String onLineStatus;

    private String deviceTypeName;

    private String subitemName;

    //物理位置
    private String areaNames;

    //逻辑位置
    private String logicalAreaNames;

    private String roomName;

}
