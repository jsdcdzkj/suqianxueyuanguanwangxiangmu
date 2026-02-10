package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WarningInfoDetails;
import lombok.Data;

@Data
public class WarningInfoDetailsVo extends WarningInfoDetails {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String warnLevelName;

    private String deviceName;

    private String areaName;
}
