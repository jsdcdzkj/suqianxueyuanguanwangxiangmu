package com.jsdc.iotpt.dto;

import lombok.Data;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2025-01-06 14:44
 **/
@Data
public class ReserveDTO {
    private Integer isDel;
    //type为1补充当天订餐信息，2：修改当天订餐信息；3：修改明天订餐信息
    private Integer type;
    private Integer userId;

}
