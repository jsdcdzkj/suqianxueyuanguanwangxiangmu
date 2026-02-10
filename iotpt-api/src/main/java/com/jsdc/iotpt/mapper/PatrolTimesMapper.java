package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.PatrolPlanDao;
import com.jsdc.iotpt.dao.PatrolTimesDao;
import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.model.PatrolTimes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PatrolTimesMapper extends BaseMapper<PatrolTimes> {

    @SelectProvider(method = "getList",type = PatrolTimesDao.class)
    List<PatrolTimes> getList(PatrolTimes patrolTime);
}
