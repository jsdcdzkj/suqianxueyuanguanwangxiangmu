package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.AlarmRecordsDao;
import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.vo.AlarmRecordsVo;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface AlarmRecordsMapper extends BaseMapper<AlarmRecords> {

    @SelectProvider(type = AlarmRecordsDao.class, method = "getEntityList")
    Page<AlarmRecords> getEntityList(Page page, AlarmRecords bean);

    @SelectProvider(type = AlarmRecordsDao.class, method = "getSubscript")
    List<AlarmRecordsVo> getSubscript(AlarmRecordsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "groupAlarmLevel")
    List<AlarmRecordsVo>groupAlarmLevel(AlarmRecordsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "line")
    List<Map<String,Object>>line(AlarmRecordsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "pieChart")
    List<AlarmStatisticsVo> pieChart(AlarmStatisticsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "areaRank")
    List<AlarmStatisticsVo> areaRank(AlarmStatisticsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "levelRank")
    List<AlarmStatisticsVo> levelRank(AlarmStatisticsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "alarmNum")
    Long alarmNum(AlarmStatisticsVo vo);
    @SelectProvider(type = AlarmRecordsDao.class, method = "trendChartDay")
    List<AlarmStatisticsVo> trendChartDay(AlarmStatisticsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "trendChartMonth")
    List<AlarmStatisticsVo> trendChartMonth(AlarmStatisticsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "averageNumber")
    Long averageNumber(AlarmStatisticsVo vo);

    @SelectProvider(type = AlarmRecordsDao.class, method = "securityAssessment")
    List<AlarmRecordsVo> securityAssessment(AlarmStatisticsVo vo);
    @SelectProvider(type = AlarmRecordsDao.class, method = "distribution")
    List<AlarmStatisticsVo> distribution(String startTime, String endTime, String radio);
    @SelectProvider(type = AlarmRecordsDao.class, method = "pieChartNoRank")
    List<AlarmStatisticsVo> pieChartNoRank(AlarmStatisticsVo vo);
}
