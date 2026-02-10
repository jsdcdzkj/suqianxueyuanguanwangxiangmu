package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigDeviceSubitem;
import lombok.Data;

/**
 * @authon thr
 * @describe 设备分项
 */
@Data
public class ConfigDeviceSubitemVo extends ConfigDeviceSubitem {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
