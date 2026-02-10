package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigSupplier;
import lombok.Data;

/**
 * @authon thr
 * @describe 供应商
 */
@Data
public class ConfigSupplierVo extends ConfigSupplier {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
