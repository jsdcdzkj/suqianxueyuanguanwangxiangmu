package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WarningDeviceMap;
import lombok.Data;

@Data
public class WarningDeviceMapVo extends WarningDeviceMap {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
