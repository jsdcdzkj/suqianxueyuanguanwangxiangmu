package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.operate.AbnormalPatrol;
import lombok.Data;

@Data
public class StallVo {

    //总车位
    private String totalCount ;
    //已用车位
    private String usedCount ;
    //空闲车位
    private String leisureCount ;
}
