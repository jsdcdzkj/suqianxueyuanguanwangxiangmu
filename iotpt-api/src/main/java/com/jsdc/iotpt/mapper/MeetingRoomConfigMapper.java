package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.MeetingRoomConfigDao;
import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.vo.MeetingReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface MeetingRoomConfigMapper extends BaseMapper<MeetingRoomConfig> {

    @SelectProvider(type = MeetingRoomConfigDao.class, method = "floorReport")
    List<MeetingReportVo> floorReport(MeetingReportVo vo);

    @SelectProvider(type = MeetingRoomConfigDao.class, method = "deptReport")
    List<MeetingReportVo> deptReport(MeetingReportVo vo);
}
