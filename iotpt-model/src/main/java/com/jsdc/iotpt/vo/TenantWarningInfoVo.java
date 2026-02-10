package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantWarningInfo;
import lombok.Data;

@Data
public class TenantWarningInfoVo extends TenantWarningInfo {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    private String tenantName;

    private Integer floorId;

    private String address;

    private String expenseTypeName;
}
