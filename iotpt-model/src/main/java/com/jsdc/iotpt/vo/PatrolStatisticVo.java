package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.PatrolStatistic;
import lombok.Data;

/**
 * ClassName: PatrolStatisticVo
 * Description:
 * date: 2024/11/28 13:48
 *
 * @author bn
 */
@Data
public class PatrolStatisticVo extends PatrolStatistic {

    private Integer pageIndex;

    private Integer pageSize;

    private String startTime;

    private String endTime;


}
