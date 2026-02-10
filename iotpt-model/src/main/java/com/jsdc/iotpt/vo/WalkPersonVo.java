package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkPerson;
import lombok.Data;


@Data
public class WalkPersonVo extends WalkPerson {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
