package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.model.operate.JobPlanArea;
import lombok.Data;

import java.util.List;

/**
 * ClassName: JobPlanVo
 * Description:
 * date: 2023/8/23 15:35
 *
 * @author bn
 */
@Data
public class JobPlanVo extends JobPlan {




//    private String selectDays;

    private String startTime;

    private String endTime;

    private List<JobPlanArea> areaList;

}
