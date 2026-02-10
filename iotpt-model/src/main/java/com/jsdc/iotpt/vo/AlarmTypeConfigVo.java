package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmTypeConfig;
import lombok.Data;

@Data
public class AlarmTypeConfigVo extends AlarmTypeConfig {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
