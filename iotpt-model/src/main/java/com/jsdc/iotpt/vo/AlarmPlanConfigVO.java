package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import lombok.Data;

import java.util.List;


@Data
public class AlarmPlanConfigVO extends AlarmPlanConfig {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String keyword;

    private List<Integer> contentIds;

}
