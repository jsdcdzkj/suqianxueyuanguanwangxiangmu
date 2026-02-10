package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ChintCloudDevicePoint;
import lombok.Data;

@Data
public class ChintCloudDevicePointVo extends ChintCloudDevicePoint {

    private Integer pageIndex;

    private Integer pageSize;

}
