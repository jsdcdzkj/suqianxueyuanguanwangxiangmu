package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 *  位置坐标对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loc {

    /**
     * 坐标点
     */
    private List<Map<String, Object>> point;
    /**
     * 坐标点个数
     */
    private Integer point_num;
    /**
     * 标点坐标系尺寸
     * get("factor_h") 坐标点坐 标系尺寸 高度
     * get("factor_w") 坐标点坐 标系尺寸 宽度
     */
    private Map<String, Object> reco_zone_size_factor;
}
