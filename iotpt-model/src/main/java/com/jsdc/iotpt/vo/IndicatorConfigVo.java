package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.IndicatorConfig;
import lombok.Data;

@Data
public class IndicatorConfigVo extends IndicatorConfig {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

}
