package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigDeviceType;
import lombok.Data;

@Data
public class ConfigDeviceTypeVo extends ConfigDeviceType {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
