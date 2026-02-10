package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.FalseAlarmRecords;
import lombok.Data;

@Data
public class FalseAlarmRecordsVo extends FalseAlarmRecords {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
