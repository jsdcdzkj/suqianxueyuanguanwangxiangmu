package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.PatrolPlanDao;
import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.vo.PatrolPlanVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface PatrolPlanMapper extends BaseMapper<PatrolPlan> {

    @SelectProvider(method = "getPatrolPlanById",type = PatrolPlanDao.class)
    PatrolPlanVo getPatrolPlanById(PatrolPlan bean);
}
