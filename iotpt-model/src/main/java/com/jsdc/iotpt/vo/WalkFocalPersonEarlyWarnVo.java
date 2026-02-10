package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkFocalPersonEarlyWarn;
import lombok.Data;


@Data
public class WalkFocalPersonEarlyWarnVo extends WalkFocalPersonEarlyWarn {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
