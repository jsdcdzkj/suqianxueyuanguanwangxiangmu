package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmPlanTimeConfig;
import lombok.Data;


@Data
public class AlarmPlanTimeConfigVO extends AlarmPlanTimeConfig {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
