package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantElectricityConfig;
import lombok.Data;

@Data
public class TenantElectricityConfigVo extends TenantElectricityConfig {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
