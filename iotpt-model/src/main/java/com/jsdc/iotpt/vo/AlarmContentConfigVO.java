package com.jsdc.iotpt.vo;


import com.jsdc.iotpt.model.new_alarm.AlarmContentConfig;
import lombok.Data;

@Data
public class AlarmContentConfigVO extends AlarmContentConfig {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String keyword;

}
