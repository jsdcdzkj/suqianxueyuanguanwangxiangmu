package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkLabel;
import lombok.Data;

@Data
public class WalkLabelVo extends WalkLabel {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
