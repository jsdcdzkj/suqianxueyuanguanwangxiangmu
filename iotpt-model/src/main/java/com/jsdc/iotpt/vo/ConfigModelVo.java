package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigModel;
import lombok.Data;

/**
 * @authon thr
 * @describe 设备型号表
 */
@Data
public class ConfigModelVo extends ConfigModel {
    private Integer configSupplierId;
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
