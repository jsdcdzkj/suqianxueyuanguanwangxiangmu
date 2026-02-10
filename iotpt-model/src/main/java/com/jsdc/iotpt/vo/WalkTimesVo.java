package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkTimes;
import lombok.Data;


/**
 *  时间组
 */
@Data
public class WalkTimesVo  extends WalkTimes {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
