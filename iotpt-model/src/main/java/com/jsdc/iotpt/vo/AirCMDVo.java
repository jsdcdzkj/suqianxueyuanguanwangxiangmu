package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class AirCMDVo {
    private List<AirConditionCMDVo> airVo;

    private AirConditionCMDVo vo;
    private int areaId;
    private int floorId;
    private String password;
}
