package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 识别信息对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reco {


    private String car_brand;

    private String car_type;
    /**
     * 识别组id
     */
    private Integer group_id;
    /**
     * 识别标识
     */
    private Integer reco_flag;
    /**
     * 识别id
     */
    private Integer reco_id;
    /**
     * 识别时间 (字符串格式时间)
     */
    private String reco_time;


}
