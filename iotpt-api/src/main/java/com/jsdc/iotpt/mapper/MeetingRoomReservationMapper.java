package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.MeetingRoomReservationDao;
import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.model.MeetingRoomReservation;
import com.jsdc.iotpt.vo.MeetingReportVo;
import com.jsdc.iotpt.vo.MeetingRoomReservationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface MeetingRoomReservationMapper extends BaseMapper<MeetingRoomReservation> {

    @SelectProvider(type = MeetingRoomReservationDao.class, method = "useCountReport")
    List<MeetingReportVo> useCountReport(MeetingReportVo vo);

    @SelectProvider(type = MeetingRoomReservationDao.class, method = "useTrendsReport")
    List<MeetingReportVo> useTrendsReport(MeetingReportVo vo);
    @SelectProvider(type = MeetingRoomReservationDao.class, method = "getMyAttendedMeetingsList")
    Page<MeetingRoomReservationVo> getMyAttendedMeetingsList(Page page,int userId, MeetingRoomReservationVo bean);


    @SelectProvider(type = MeetingRoomReservationDao.class, method = "meetingRoomFrequency")
    List<MeetingReportVo> meetingRoomFrequency(MeetingReportVo vo);
    @SelectProvider(type = MeetingRoomReservationDao.class, method = "getMeetingHomePage")
    Page<MeetingRoomReservationVo> getMeetingHomePage(Page page,int userId, MeetingRoomReservationVo bean);

    @SelectProvider(type = MeetingRoomReservationDao.class, method = "meetingFloor")
    List<MeetingRoomConfig> meetingFloor();
}
