package com.jsdc.iotpt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.AlarmGroupDao;
import com.jsdc.iotpt.dao.SysBuildFloorDao;
import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface AlarmGroupMapper extends BaseMapper<AlarmGroup> {
    @SelectProvider(type = AlarmGroupDao.class, method = "selectGroupByFloorId")
    List<AlarmStatisticsVo> selectGroupByFloorId(Integer id);
}
