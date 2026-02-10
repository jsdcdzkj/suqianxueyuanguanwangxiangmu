package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.mapper.MeetingRoomReservationMapper;
import com.jsdc.iotpt.model.DataModelDays;
import com.jsdc.iotpt.model.MeetingRoomReservation;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.StringUtils;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 大数据模型数据
 * 每日生成数据
 */
@Service
@Transactional
public class DataModelDaysService extends BaseService<DataModelDays> {

    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private MeetingRoomReservationMapper meetingRoomReservationMapper;

//    public static void main(String[] args) {
//        Date startDate = new DateTime("2024-06-01T00:00:00").toDate();
//        Date endDate = new Date();
//        while (startDate.before(endDate)) {
//            System.out.println(new DateTime(startDate).toString("yyyy-MM-dd HH:mm:ss"));
//            //季节
//            int month = startDate.getMonth() + 1;
//            if (month >= 3 && month <= 5) {
//                System.out.println("春季");
//            } else if (month >= 6 && month <= 8) {
//                System.out.println("夏季");
//            } else if (month >= 9 && month <= 11) {
//                System.out.println("秋季");
//            } else {
//                System.out.println("冬季");
//            }
//
//            String time = new DateTime(startDate).toString("yyyy-MM-dd");
//            Date startTime = new DateTime(time + "T08:30:00").toDate();
//            Date endTime = new DateTime(time + "T17:30:00").toDate();
//
//            // 计算时间差
//            Duration duration = Duration.between(startTime.toInstant(), endTime.toInstant());
//            // 获取时间差的小时数
//            long minutes = duration.toMinutes();
//            System.out.println("获取时间差的小时数：" + new BigDecimal(minutes).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP));
//
//            System.out.println("会议：" + DateUtils.getStartTimeOfCurrentDay(startDate));
//            System.out.println("会议：" + DateUtils.getEndTimeOfCurrentDay(startDate));
//
//            Date startTime2 = new DateTime("2024-05-22 20:00:00".replaceAll(" ", "T")).toDate();
//            Date endTime2 = new DateTime("2024-05-22 20:30:00".replaceAll(" ", "T")).toDate();
//            // 计算时间差
//            Duration duration2 = Duration.between(startTime2.toInstant(), endTime2.toInstant());
//            // 获取时间差的小时数
//            long minutes2 = duration2.toMinutes();
//            System.out.println("会议预约时长：" + new BigDecimal(minutes2).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP));
//
//            startDate = new DateTime(startDate).plusDays(1).toDate();
//
//        }
//    }

    /**
     * 大数据模型数据
     * 每日生成数据
     */
    public void createData() {
        Date startDate = new DateTime("2024-06-01T00:00:00").toDate();
        Date endDate = new Date();
        while (startDate.before(endDate)) {
            System.out.println(new DateTime(startDate).toString("yyyy-MM-dd"));

            DataModelDays dataModelDays = new DataModelDays();
            dataModelDays.setTime(new DateTime(startDate).toString("yyyy-MM-dd"));
            dataModelDays.setCreateTime(new Date());
            dataModelDays.setIsDel(0);
            //季节
            int month = startDate.getMonth() + 1;
            if (month >= 3 && month <= 5) {
                dataModelDays.setSeason("春季");
            } else if (month >= 6 && month <= 8) {
                dataModelDays.setSeason("夏季");
            } else if (month >= 9 && month <= 11) {
                dataModelDays.setSeason("秋季");
            } else {
                dataModelDays.setSeason("冬季");
            }

            //每日用电
            String powerVal = queryPowerData(new DateTime(startDate).toString("yyyy-MM-dd HH:mm:ss"), new DateTime(startDate).plusDays(1).toString("yyyy-MM-dd HH:mm:ss"));
            dataModelDays.setPowerVal(powerVal);

            //室内温度
            String inTemp = queryTempData(new DateTime(startDate).toString("yyyy-MM-dd 08:00:00"), new DateTime(startDate).toString("yyyy-MM-dd 18:00:00"), "200149");
            //室外温度
            String outTemp = queryTempData(new DateTime(startDate).toString("yyyy-MM-dd 08:00:00"), new DateTime(startDate).toString("yyyy-MM-dd 18:00:00"), "200358");
            dataModelDays.setInTemp(inTemp);
            dataModelDays.setOutTemp(outTemp);

            //工作时长、工作日
            getSumWorkingHours(new DateTime(startDate).toString("yyyy-MM-dd"), dataModelDays);

            //会议预约总时长
            getMeetingDurations(startDate, dataModelDays);

            if (dataModelDays.getRoom_reserve_count() == 0) {
                Integer count = queryBoxData("8F鼎悦", new DateTime(startDate).toString("yyyy-MM-dd HH:mm:ss"), new DateTime(startDate).plusDays(1).toString("yyyy-MM-dd HH:mm:ss"));
                Integer count2 = queryBoxData("8F驰悦", new DateTime(startDate).toString("yyyy-MM-dd HH:mm:ss"), new DateTime(startDate).plusDays(1).toString("yyyy-MM-dd HH:mm:ss"));
                Integer count3 = queryBoxData("16F望月", new DateTime(startDate).toString("yyyy-MM-dd HH:mm:ss"), new DateTime(startDate).plusDays(1).toString("yyyy-MM-dd HH:mm:ss"));
                Integer count4 = queryBoxData("16F观澜", new DateTime(startDate).toString("yyyy-MM-dd HH:mm:ss"), new DateTime(startDate).plusDays(1).toString("yyyy-MM-dd HH:mm:ss"));

                dataModelDays.setRoom_reserve_count(count + count2 + count3 + count4);
            }
            save(dataModelDays);
            startDate = new DateTime(startDate).plusDays(1).toDate();
        }
    }

    /**
     * 大数据模型数据 每日用电
     * 用电量(大楼总电表+备用电表)
     */
    public String queryPowerData(String startTime, String endTime) {
        String sql = "SELECT sum(val) as val from " +
                " (select last(val)-first(val) as val from datasheet " +
                " where channelId='ENERGY_TOTAL' " +
                " and (deviceCollectId='200363' or deviceCollectId='200364') ";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " and time >= '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " and time < '" + endTime + "'";
        }
        sql += " group by deviceCollectId)";
        QueryResult query = influxdbService.query(sql);

        List<Map<String, Object>> mapList = influxdbService.queryResultProcess(query);

        DecimalFormat df = new DecimalFormat("0.00");
        double sum = 0;
        for (Map<String, Object> map : mapList) {
            if (StringUtils.isNotNull(map.get("val"))) {
                double val = (double) map.get("val");
                sum += val;
            }
        }
        return df.format(sum);
    }

    /**
     * 室内温度、室外温度
     */
    public String queryTempData(String startTime, String endTime, String deviceCollectId) {
        String sql = "select mean(val) as val from datasheet where channelId='TEMP' ";
        sql += " and deviceCollectId = '" + deviceCollectId + "'";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " and time >= '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " and time <= '" + endTime + "'";
        }
        QueryResult query = influxdbService.query(sql);

        List<Map<String, Object>> mapList = influxdbService.queryResultProcess(query);

        DecimalFormat df = new DecimalFormat("0.00");
        double sum = 0;
        for (Map<String, Object> map : mapList) {
            if (StringUtils.isNotNull(map.get("val"))) {
                double val = (double) map.get("val");
                sum += val;
            }
        }
        return df.format(sum);
    }

    /**
     * 工作时长、工作日
     */
    public void getSumWorkingHours(String time, DataModelDays dataModelDays) {
        //软件部门人员
        SysUser sysUser2 = new SysUser();
        sysUser2.setDeptId(3040);
        List<SysUser> userList2 = sysUserService.getList(sysUser2);
        List<Integer> idList = userList2.stream().map(SysUser::getId).collect(Collectors.toList());
        //1上午上班
        //软件部门人员，超过5人打卡上班，就算本日期为工作日

        long totalMinutes = 0;
        List<SysUser> userList = sysUserService.getList(new SysUser());
        int count = 0;
        for (SysUser sysUser : userList) {

        }

        dataModelDays.setUserCounts(String.valueOf(count));
        if (count == 0 || totalMinutes == 0) {
            dataModelDays.setWorkingHours("0");
        } else {
            //总时长 / 总人数
            BigDecimal avgHours = new BigDecimal(String.valueOf(totalMinutes)).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(String.valueOf(count)), 2, BigDecimal.ROUND_HALF_UP);
            dataModelDays.setWorkingHours(avgHours.toString());
        }
    }

    /**
     * 会议预约总时长
     */
    public void getMeetingDurations(Date time, DataModelDays dataModelDays) {
        List<MeetingRoomReservation> list = meetingRoomReservationMapper.selectList(Wrappers.<MeetingRoomReservation>lambdaQuery()
                .eq(MeetingRoomReservation::getIsDel, 0)
                .ge(MeetingRoomReservation::getStartTime, DateUtils.getStartTimeOfCurrentDay(time))
                .le(MeetingRoomReservation::getEndTime, DateUtils.getEndTimeOfCurrentDay(time))
                .orderByDesc(MeetingRoomReservation::getStartTime));

        long meetingDurations = 0;
        if (list.size() > 0) {
            for (MeetingRoomReservation reservation : list) {
                if (StringUtils.isNotEmpty(reservation.getStartTime()) && StringUtils.isNotEmpty(reservation.getEndTime())) {
                    Date startTime = new DateTime((reservation.getStartTime() + ":00").replaceAll(" ", "T")).toDate();
                    Date endTime = new DateTime((reservation.getEndTime() + ":00").replaceAll(" ", "T")).toDate();
                    // 计算时间差
                    Duration duration = Duration.between(startTime.toInstant(), endTime.toInstant());
                    // 获取时间差的分钟数
                    long minutes = duration.toMinutes();
                    meetingDurations = meetingDurations + minutes;
                }
            }
        }
        if (meetingDurations > 0) {
            dataModelDays.setMeeting_reservation_duration(new BigDecimal(String.valueOf(meetingDurations)).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP).toString());
        } else {
            dataModelDays.setMeeting_reservation_duration("0");
        }

    }


    /**
     * 包厢用电
     * 8F鼎悦 200495 18人包间A 200496 18人包间B
     * 8F驰悦 200493 16人包间A 200494 16人包间B
     * 16F望月 200640,200641,200642,200643,
     * 16F观澜 200626,200627,200628,200630,200644
     */
    public Integer queryBoxData(String boxName, String startTime, String endTime) {
        String sql = "SELECT sum(val) as val from " +
                " (select last(val)-first(val) as val from datasheet " +
                " where channelId='ENERGY_TOTAL' ";
        if ("8F鼎悦".equals(boxName)) {
            sql += " and (deviceCollectId='200495' or deviceCollectId='200496') ";
        } else if ("8F驰悦".equals(boxName)) {
            sql += " and (deviceCollectId='200493' or deviceCollectId='200494') ";
        } else if ("16F望月".equals(boxName)) {
            sql += " and (deviceCollectId='200640' or deviceCollectId='200641' or deviceCollectId='200642' or deviceCollectId='200643') ";
        } else if ("16F观澜".equals(boxName)) {
            sql += " and (deviceCollectId='200626' or deviceCollectId='200627' or deviceCollectId='200628' or deviceCollectId='200630' or deviceCollectId='200644') ";
        }
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " and time >= '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " and time < '" + endTime + "'";
        }
        sql += " group by deviceCollectId)";
        QueryResult query = influxdbService.query(sql);

        List<Map<String, Object>> mapList = influxdbService.queryResultProcess(query);

        DecimalFormat df = new DecimalFormat("0.00");
        double sum = 0;
        for (Map<String, Object> map : mapList) {
            if (StringUtils.isNotNull(map.get("val"))) {
                double val = (double) map.get("val");
                sum += val;
            }
        }

        int count = 0;
        if (sum >= 5d) {
            count = 1;
        }
        return count;
    }

}
