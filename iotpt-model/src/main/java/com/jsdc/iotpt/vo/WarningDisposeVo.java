package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WarningDispose;
import lombok.Data;

@Data
public class WarningDisposeVo extends WarningDispose {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
