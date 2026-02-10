package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.MissionItemRecordDao;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.MissionItemRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface MissionItemRecordMapper extends BaseMapper<MissionItemRecord> {

    @SelectProvider(type = MissionItemRecordDao.class, method = "selectByMissionIdGroup")
    List<MissionItemRecord> selectByMissionIdGroup(Integer missionId);


    @SelectProvider(type = MissionItemRecordDao.class, method = "getRecordByMissionId")
    List<MissionItemRecord> getRecordByMissionId(Mission mission);


    @SelectProvider(type = MissionItemRecordDao.class, method = "selectMissionCount")
    Integer selectMissionCount(Integer missionId, Integer isHandle);
}
