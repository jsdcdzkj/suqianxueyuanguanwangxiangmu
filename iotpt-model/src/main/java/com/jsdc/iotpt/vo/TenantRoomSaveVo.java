package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantRoom;
import com.jsdc.iotpt.model.TenantRoomDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class TenantRoomSaveVo extends TenantRoom {

    private List<TenantRoomDevice> electricityDeviceList;

}
