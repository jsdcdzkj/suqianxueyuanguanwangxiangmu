package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigTenantNotice;
import lombok.Data;

@Data
public class ConfigTenantNoticeVo extends ConfigTenantNotice {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
