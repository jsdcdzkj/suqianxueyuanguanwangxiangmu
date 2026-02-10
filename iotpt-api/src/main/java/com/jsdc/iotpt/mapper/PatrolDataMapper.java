package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.PatrolData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface PatrolDataMapper extends BaseMapper<PatrolData> {

    @Select("SELECT MAX(patrolDataId) AS maxValue FROM patrol_statistic")
    Integer getMaxValueOfColumn();
}
