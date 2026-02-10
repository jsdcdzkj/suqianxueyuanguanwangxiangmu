package com.jsdc.iotpt.dto;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantStagePriceDTO {

    private DateTime beginTime;

    private DateTime endTime;

    private BigDecimal price;

}
