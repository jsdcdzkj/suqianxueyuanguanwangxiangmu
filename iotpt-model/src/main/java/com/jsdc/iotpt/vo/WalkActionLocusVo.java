package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkActionLocus;
import lombok.Data;


@Data
public class WalkActionLocusVo extends WalkActionLocus {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
