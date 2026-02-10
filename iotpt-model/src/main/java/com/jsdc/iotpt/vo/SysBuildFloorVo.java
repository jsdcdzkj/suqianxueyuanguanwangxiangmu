package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.sys.SysBuildFloor;
import lombok.Data;

import java.util.List;

@Data
public class SysBuildFloorVo extends SysBuildFloor {
    private List<AlarmStatisticsVo> selectGroupByFloorId;
}
