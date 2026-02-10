package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import lombok.Data;

@Data
public class AlarmGroupVO extends AlarmGroup {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
