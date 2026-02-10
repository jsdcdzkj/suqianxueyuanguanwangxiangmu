package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantRoom;
import lombok.Data;

@Data
public class TenantRoomVo extends TenantRoom {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    private String address;

    private String electricityMeter;

}