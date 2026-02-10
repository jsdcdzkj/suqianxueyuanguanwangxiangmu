package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WalkEquipment;
import lombok.Data;


@Data
public class WalkEquipmentVo extends WalkEquipment {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
