package com.jsdc.iotpt.vo;

import lombok.Data;

/**
 * @Author ：苹果
 * @Description：
 * @Date ：2023/7/14 9:54
 * @Modified By：
 */
@Data
public class WaterElectricityByFloorVo {
    private String floor;

    private String floorName;

    private String electricity="0";

    private String water="0";
}
