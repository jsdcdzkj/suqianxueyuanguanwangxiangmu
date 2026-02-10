package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 车位信息对象
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parking {

    private Map<String,Object> loc;

    private Integer parking_state;

    private Integer zone_id;

    private String zone_name;
}
