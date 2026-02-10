package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.DeviceAccessControl;
import lombok.Data;

@Data
public class DeviceAccessControlVo extends DeviceAccessControl {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
