package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.PatrolReport;
import com.jsdc.iotpt.model.PatrolStatistic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatrolStatisticMapper  extends BaseMapper<PatrolStatistic> {
}
