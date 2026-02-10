package com.jsdc.iotpt.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片分辨率
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resolution {

    /**
     * 图片高
     */
    private Integer height;
    /**
     * 图片宽
     */
    private Integer width;

}
