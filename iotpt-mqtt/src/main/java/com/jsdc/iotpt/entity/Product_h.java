package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 路内产品信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product_h {

    /**
     * 辆信息对象
     * car_pos.get("pos")  --->  车头车尾信息  0:无效; 1:车头; 2:车尾
     */
    private Map<String, Object> car_pos;
    /**
     * 车位信息对象
     */
    private Parking parking;
    /**
        车牌信息对象
     */
    private Plate plate;
    /**
     * 识别信息对象
     */
    private Reco reco;
}
