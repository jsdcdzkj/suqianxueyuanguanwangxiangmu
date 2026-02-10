package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.PatrolDeviceOrPoint;
import com.jsdc.iotpt.model.PatrolPeople;
import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.model.PatrolTimes;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: PatrolPlanVo
 * Description:
 * date: 2024/1/9 16:27
 *
 * @author bn
 */
@Data
public class PatrolPlanVo extends PatrolPlan {

    private List<String> cycleTimes=new ArrayList<>();

    private List<PatrolPeople> patrolPeoples=new ArrayList<>();

    private List<PatrolDeviceOrPoint> patrolDeviceOrPoints=new ArrayList<>();

    private List<PatrolTimes> patrolTimes=new ArrayList<>();

    private Integer pageNum;

    private Integer pageSize;

}
