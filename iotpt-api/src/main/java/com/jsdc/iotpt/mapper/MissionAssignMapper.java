package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.MissionAssignDao;
import com.jsdc.iotpt.model.operate.MissionAssign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface MissionAssignMapper extends BaseMapper<MissionAssign> {
    @SelectProvider(type = MissionAssignDao.class, method = "getGroupName")
    String getGroupName(Integer missionId);

    @SelectProvider(type = MissionAssignDao.class, method = "selecMissiontList")
    List<MissionAssign> selecMissiontList();
}
