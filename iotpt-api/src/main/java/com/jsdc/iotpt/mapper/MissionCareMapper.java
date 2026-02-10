package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.MissionCareDao;
import com.jsdc.iotpt.model.operate.MissionCare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface MissionCareMapper extends BaseMapper<MissionCare> {
    @SelectProvider(type = MissionCareDao.class, method = "countCare")
    Long countCare(Integer userId);
}
