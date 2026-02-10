package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmCategory;
import lombok.Data;

@Data
public class AlarmCategoryVO extends AlarmCategory {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
