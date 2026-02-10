package com.jsdc.iotpt.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChintCloudDevicePointOpearteVo {

    @ApiModelProperty("点位列表 storagePoint")
    private List<String> pointList;

    @ApiModelProperty("值（0-关，1-开）")
    private String value;

}
