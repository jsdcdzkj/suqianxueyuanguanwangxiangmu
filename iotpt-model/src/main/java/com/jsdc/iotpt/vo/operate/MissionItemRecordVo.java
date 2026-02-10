package com.jsdc.iotpt.vo.operate;

import com.jsdc.iotpt.model.operate.MissionItemRecord;
import lombok.Data;

import java.util.Date;


/**
 * 任务项记录
 */
@Data
public class MissionItemRecordVo extends MissionItemRecord {

    //班组id
    private Integer teamGroupsId;
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //处理时间
    private Date handleDate;

}
