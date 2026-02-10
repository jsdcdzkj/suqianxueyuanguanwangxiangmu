package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.MissionDao;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.MissionTjVo;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import com.jsdc.iotpt.vo.ElectricalSafetyReportVo;
import com.jsdc.iotpt.vo.ElectricalSafetyReportVo2;
import com.jsdc.iotpt.vo.ReportManageVo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import com.jsdc.iotpt.vo.operate.WorkOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MissionMapper extends BaseMapper<Mission> {

    @SelectProvider(type = MissionDao.class, method = "getListByGroup")
    List<MissionTjVo> getListByGroup(MissionTjVo missionVo);

    @SelectProvider(type = MissionDao.class, method = "stateCount")
    MissionTjVo stateCount(MissionTjVo missionVo);

    //App端关注功能
    @SelectProvider(type = MissionDao.class, method = "pageCareMission")
    Page<MissionVo> pageCareMission(Page page, Mission bean);

    //分页查询
    @SelectProvider(type = MissionDao.class, method = "missionPageList")
    Page<MissionVo> missionPageList(Page page, Mission bean);

    //分页查询
    @SelectProvider(type = MissionDao.class, method = "pageListMission")
    Page<MissionVo> pageListMission(Page page, Mission bean);
    //分页查询
    @SelectProvider(type = MissionDao.class, method = "pageListMission1")
    Page<MissionVo> pageListMission1(Page page, Mission bean);

    //分页查询
    @SelectProvider(type = MissionDao.class, method = "countHandle")
    List<MissionVo> countHandle();

    //分页查询
    @SelectProvider(type = MissionDao.class, method = "countAllTask")
    List<MissionVo> countAllTask();

    //查询
    @SelectProvider(type = MissionDao.class, method = "listMission")
    List<Map<String, Object>> listMission(Mission bean);

    //分页查询
    @SelectProvider(type = MissionDao.class, method = "pageListMissionApp")
    Page<MissionVo> pageListMissionApp(Page page, Mission bean);

    //分页查询
    @SelectProvider(type = MissionDao.class, method = "myPendingList")
    Page<MissionVo> myPendingList(Page page, Mission bean);

    //统计分组总数
    @SelectProvider(type = MissionDao.class, method = "countGroupsId")
    Integer countGroupsId(List<Integer> teamgroupIds, List<Integer> states);

    /**
     * 电气安全分析 派单各个状态数量统计
     *
     * @return
     */
    @SelectProvider(type = MissionDao.class, method = "stateCountReport")
    ElectricalSafetyReportVo stateCountReport(ElectricalSafetyReportVo vo);

    /**
     * 电气安全分析 派单各个状态数量统计
     *
     * @return
     */
    @SelectProvider(type = MissionDao.class, method = "getListTop")
    List<ElectricalSafetyReportVo> getListTop(ElectricalSafetyReportVo vo);


    @SelectProvider(type = MissionDao.class, method = "getListByDeviceIdReport2")
    List<ElectricalSafetyReportVo2> getListByDeviceIdReport2(ElectricalSafetyReportVo2 vo);

    @SelectProvider(type = MissionDao.class, method = "getList")
    Page<MissionTjVo> getList(Page page, MissionTjVo missionVo);

    @SelectProvider(type = MissionDao.class, method = "getList2")
    List<MissionTjVo> getList2(MissionTjVo missionVo);


    @SelectProvider(type = MissionDao.class, method = "getCount")
    List<MissionTjVo> getCount(MissionTjVo missionVo);

    @SelectProvider(type = MissionDao.class, method = "rwqs")
    MissionTjVo rwqs(MissionTjVo missionVo);


    @SelectProvider(type = MissionDao.class, method = "finishingRate")
    List<MissionTjVo> finishingRate(MissionTjVo missionVo);

    @SelectProvider(type = MissionDao.class, method = "getMission_type")
    List<HashMap> getMission_type(String timeType, String timeStart, String timeEnd);

    @SelectProvider(type = MissionDao.class, method = "getMission_state")
    List<HashMap> getMission_state(String timeType, String timeStart, String timeEnd);

    @SelectProvider(type = MissionDao.class, method = "getMission_type_team")
    List<HashMap> getMission_type_team(String timeType, String timeStart, String timeEnd);

    @SelectProvider(type = MissionDao.class, method = "getSendOrdersCount")
    MissionTjVo getSendOrdersCount(String date);

    @SelectProvider(type = MissionDao.class, method = "getCompleteCount")
    MissionTjVo getCompleteCount(String date);

    @SelectProvider(type = MissionDao.class, method = "numberGjOfDays")
    List<Map<String, Object>> numberGjOfDays();

    @SelectProvider(type = MissionDao.class, method = "monthAlarm")
    List<Map<String, Object>> monthAlarm();

    @SelectProvider(type = MissionDao.class, method = "alarmStatisticsNum")
    Map<String, Object> alarmStatisticsNum();

    @SelectProvider(type = MissionDao.class, method = "equipmentStatisticsNum")
    Map<String, Object> equipmentStatisticsNum();

    @SelectProvider(type = MissionDao.class, method = "merAlarmNum")
    long merAlarmNum(int num);



    //处理查询
    @SelectProvider(type = MissionDao.class, method = "dclCount")
    long dclCount( Mission bean);

    @SelectProvider(type = MissionDao.class, method = "appRepairPageList")
    Page<MissionVo> appRepairPageList(SysUser user,Page page, MissionVo bean);

    @SelectProvider(type = MissionDao.class, method = "appMissionPageList")
    Page<MissionVo> appMissionPageList(Page page, MissionVo bean);

    @SelectProvider(type = MissionDao.class, method = "pendingList")
    Page<MissionVo> pendingList(Page page, MissionVo bean);

    @SelectProvider(type = MissionDao.class, method = "gdtjs")
    List<String> gdtjs();

    @SelectProvider(type = MissionDao.class, method = "totalTasksJSC")
    List<String> totalTasksJSC(String startTime, String endTime, String areaId);

    @SelectProvider(type = MissionDao.class, method = "missionTypeCount")
    List<String> missionTypeCount(String startTime,String endTime);

    @SelectProvider(type = MissionDao.class, method = "missionTypeCount2")
    MissionVo missionTypeCount2(String startTime,String endTime);



    @SelectProvider(type = MissionDao.class, method = "responseSpeed")
    List<Map> responseSpeed(String startTime,String endTime);

    @SelectProvider(type = MissionDao.class, method = "completionStatus")
    List<Map> completionStatus(MissionVo bean);

    @SelectProvider(type = MissionDao.class, method = "abnormalEquipment")
    List<MissionVo> abnormalEquipment();

    @SelectProvider(type = MissionDao.class, method = "listData")
    List<MissionVo> listData();

    @SelectProvider(type = MissionDao.class, method = "listData2")
    List<MissionVo> listData2();


    @SelectProvider(type = MissionDao.class, method = "timeData")
    List<String> timeData(String startTime,String endTime);

    @SelectProvider(type = MissionDao.class, method = "timeData2")
    List<String> timeData2(String startTime,String endTime);

    @SelectProvider(type = MissionDao.class, method = "timeData3")
    List<String> timeData3(String startTime,String endTime);

    @SelectProvider(type = MissionDao.class, method = "trendsMonth")
    List<Map> trendsMonth();

    @SelectProvider(type = MissionDao.class, method = "trendsYear")
    List<Map> trendsYear();

    //App端我的上报
    @SelectProvider(type = MissionDao.class, method = "pageReportList")
    Page<MissionVo> pageReportList(Page page, Mission bean);

    @SelectProvider(type = MissionDao.class, method = "getMissionViews")
    List<MissionVo> getMissionViews(ReportManageVo bean);

    @SelectProvider(type = MissionDao.class, method = "getErrorMissions")
    List<MissionVo> getErrorMissions(List<Integer> taskMissionIds);

    @SelectProvider(type = MissionDao.class, method = "avgXiangYingTime")
    String avgXiangYingTime();

    @SelectProvider(type = MissionDao.class, method = "isNotWarn")
    Integer isNotWarn();

    @SelectProvider(type = MissionDao.class, method = "warnHuiFu")
    String warnHuiFu();

    @SelectProvider(type = MissionDao.class, method = "avgHuiFuTime")
    String avgHuiFuTime();

    @SelectProvider(type = MissionDao.class, method = "pageWorkOrderList")
    Page<WorkOrderVo> pageWorkOrderList(Page page, WorkOrderVo vo,List<String> roleFlags);


    @SelectProvider(type = MissionDao.class, method = "workSourceCount")
    List<Integer> workSourceCount(Integer large,Integer small,Integer userId,List<String> roleFlags);

    @SelectProvider(type = MissionDao.class, method = "missionType")
    List<Map<String,Object>> missionType();

    @SelectProvider(type = MissionDao.class, method = "missionSource")
    List<Map<String,Object>> missionSource();

    @SelectProvider(type = MissionDao.class, method = "missionTrend")
    List<Map<String,Object>> missionTrend();

    @SelectProvider(type = MissionDao.class, method = "missionTrendLast")
    List<Map<String,Object>> missionTrendLast();

    @SelectProvider(type = MissionDao.class, method = "undispatchedOrderCpount")
    String undispatchedOrderCpount(String states,String userId);

    @SelectProvider(type = MissionDao.class, method = "overdueMissionCount")
    String overdueMissionCount(String userId);

    @SelectProvider(type = MissionDao.class, method = "missionTimeCount")
    String missionTimeCount(Integer day,String userId);

    @SelectProvider(type = MissionDao.class, method = "missionCount")
    String missionCount(String deptId);

    @SelectProvider(type = MissionDao.class, method = "missionTodayCount")
    String missionTodayCount(String deptId);

    @SelectProvider(type = MissionDao.class, method = "missionCount2")
    String missionCount2(String startTime,String endTime);



    //分页查询
    @SelectProvider(type = MissionDao.class, method = "pageListMissionNoPage")
    MissionVo pageListMissionNoPage(Mission bean);

    @SelectProvider(type = MissionDao.class, method = "alarmAvgXiangYingTime")
    String alarmAvgXiangYingTime(AlarmStatisticsVo vo);

    @SelectProvider(type = MissionDao.class, method = "alarmHuiFu")
    String alarmHuiFu(AlarmStatisticsVo vo);

    @SelectProvider(type = MissionDao.class, method = "alarmAvgHuiFuTime")
    String alarmAvgHuiFuTime(AlarmStatisticsVo vo);

    @SelectProvider(type = MissionDao.class, method = "alarmIsNotWarn")
    Integer alarmIsNotWarn(AlarmStatisticsVo vo);
}
