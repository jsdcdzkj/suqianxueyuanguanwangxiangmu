package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.JsdcDateUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.MeetingReportVo;
import com.jsdc.iotpt.vo.MeetingRoomReservationVo;
import org.springframework.boot.logging.java.SimpleFormatter;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public class MeetingRoomReservationDao {

    /**
     * 使用频率 日月年
     *
     * @return
     */
    public String useCountReport(MeetingReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "\tf.id,\n" +
                "\tmax( f.floorName ) name,\n" +
                "\tmax( f.SORT ),\n" +
                "\tcount( r.id ) value \n" +
                "FROM\n" +
                "\tSYS_BUILD_FLOOR f\n" +
                "\tLEFT JOIN MEETING_ROOM_CONFIG m ON m.AREAID = f.id \n" +
                "\tAND m.ISDEL = 0\n" +
                "\tLEFT JOIN MEETING_ROOM_RESERVATION r ON r.roomId = m.id \n" +
                "\tAND r.ISDEL = 0 ");
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                sql.append(" AND r.startTime >= '").append(vo.getStartTime()).append("'");
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                sql.append(" AND r.startTime < '").append(vo.getEndTime()).append("'");
            }
        }
        sql.append(" WHERE\n" +
                "\tf.ISDEL = 0 \n" +
                "GROUP BY\n" +
                "\tf.id \n" +
                "ORDER BY\n" +
                "\tvalue DESC,\n" +
                "\tmax( f.SORT )");

        return sql.toString();
    }


    /**
     * 会议室使用趋势分析 日月年
     *
     * @return
     */
    public String useTrendsReport(MeetingReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "\tcount( r.id ) value,\n" +
                "\tr.roomId name \n" +
                "FROM\n" +
                "\tMEETING_ROOM_RESERVATION r \n" +
                "WHERE\n" +
                "\tr.ISDEL = 0 \n");
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                sql.append(" AND r.startTime <= '").append(vo.getEndTime()).append("'");
            }
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                sql.append(" AND r.endTime >= '").append(vo.getStartTime()).append("'");
            }
        }
        sql.append(" GROUP BY\n" +
                "\tr.roomId");

        return sql.toString();
    }

    /**
     * 获取我参与的会议
     * @param userId
     * @return
     */
    public String getMyAttendedMeetingsList(Page page,int userId, MeetingRoomReservationVo bean) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select DISTINCT r.* from ");
        sql.append(" MEETING_ROOM_RESERVATION r LEFT JOIN MEETING_ROOM_RESERVATION_USER u ");
        sql.append(" on r.id= u.reservationId ");
        sql.append(" where 1=1 ");
        // 1 全部 2 我发起的 3 我参与的
        if (bean.getListType()==1){
            sql.append(" AND (u.USERID =  ").append(userId).append(" OR r.RESERVATIONPERSON = ").append(userId).append(")");

        }else if (bean.getListType()==2){
            sql.append(" AND r.RESERVATIONPERSON = ").append(userId);
        }else {
            sql.append(" AND u.USERID =  ").append(userId);
        }
        //会议状态 0 全部 其他按照 会议状态
        if (bean.getRoomStatus() != 0){
            sql.append(" AND r.ROOMSTATUS =  ").append(bean.getRoomStatus());
        }
        sql.append(" and  r.ISDEL = 0 ");
        if (bean.getTodayFlag().equals("1")){
            // 得到今天的日期
            Date date = new Date();
            String dateFormat = JsdcDateUtil.getDateFormat(date, JsdcDateUtil.FULL_SPLIT_PATTERN);
            sql.append(" and r.startTime >= '").append(dateFormat+" 00:00:00").append("' and ").append(" r.endTime <= '").append(dateFormat+" 23:59:59").append("'");
            if (bean.getSequence() == 0){
                sql.append(" order By r.startTime asc ");
            }else {
                sql.append(" order By r.startTime desc ");
            }
        }else {
            if (bean.getSequence() == 0){
                sql.append(" order By r.startTime asc ");
            }else {
                sql.append(" order By r.startTime desc ");
            }

        }
        return sql.toString();
    }


    /**
     * 会议室使用频率
     * 使用频率 日月年
     *
     * @return
     */
    public String meetingRoomFrequency(MeetingReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "    mrc.ROOMNAME as name,\n" +
                "    NVL(reservation_count, 0) AS count\n" +
                "FROM\n" +
                "    MEETING_ROOM_CONFIG mrc\n" +
                "LEFT JOIN (\n" +
                "    SELECT\n" +
                "        ROOMID,\n" +
                "        COUNT(STARTTIME) AS reservation_count\n" +
                "    FROM\n" +
                "        MEETING_ROOM_RESERVATION\n" +
                "    WHERE 1=1 ");
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                sql.append(" AND TO_DATE(STARTTIME, 'YYYY-MM-DD HH24:MI:SS') >= TO_DATE('"+vo.getStartTime()+"', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                sql.append(" AND TO_DATE(STARTTIME, 'YYYY-MM-DD HH24:MI:SS') < TO_DATE('"+vo.getEndTime()+"', 'YYYY-MM-DD HH24:MI:SS')");
            }
        }
        sql.append(" GROUP BY\n" +
                "        ROOMID\n" +
                ") mrr ON mrc.ID = mrr.ROOMID\n" +
                "WHERE\n" +
                "    mrc.ISDEL = 0\n" +
                "ORDER BY\n" +
                "    count DESC");

        return sql.toString();
    }

    /**
     * 获取我参与的会议
     * @param userId
     * @return
     */
    public String getMeetingHomePage(Page page,int userId, MeetingRoomReservationVo bean) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select DISTINCT r.* from ");
        sql.append(" MEETING_ROOM_RESERVATION r LEFT JOIN MEETING_ROOM_RESERVATION_USER u ");
        sql.append(" on r.id= u.reservationId ");
        sql.append(" where 1=1 ");
        // 1 全部 2 我发起的 3 我参与的
        if (bean.getListType()==1){
            sql.append(" AND (u.USERID =  ").append(userId).append(" OR r.RESERVATIONPERSON = ").append(userId).append(")");

        }else if (bean.getListType()==2){
            sql.append(" AND r.RESERVATIONPERSON = ").append(userId);
        }else {
            sql.append(" AND u.USERID =  ").append(userId);
        }
        //会议状态 0 全部 其他按照 会议状态
//        if (bean.getRoomStatus() != 0){
            sql.append(" AND (r.ROOMSTATUS = 1 or r.ROOMSTATUS = 2) ");
//        }
        sql.append(" and  r.ISDEL = 0 ");
        if (bean.getTodayFlag().equals("1")){
            // 得到今天的日期
            Date date = new Date();
            String dateFormat = JsdcDateUtil.getDateFormat(date, JsdcDateUtil.FULL_SPLIT_PATTERN);
            sql.append(" and r.startTime >= '").append(dateFormat+" 00:00:00").append("' and ").append(" r.endTime <= '").append(dateFormat+" 23:59:59").append("'");
            if (bean.getSequence() == 0){
                sql.append(" order By r.startTime asc ");
            }else {
                sql.append(" order By r.startTime desc ");
            }
        }else {
            if (bean.getSequence() == 0){
                sql.append(" order By r.startTime asc ");
            }else {
                sql.append(" order By r.startTime desc ");
            }

        }
        return sql.toString();
    }


    /**
     * @return
     */
    public String meetingFloor() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT\n" +
                "\tMIN( floor.floorName ) AS floorName,\n" +
                "\tmrc.FLOORID,\n" +
                "\tCOUNT( mrc.FLOORID ) AS roomNum , MIN(floor.SORT) SORT\n" +
                "FROM\n" +
                "\tMEETING_ROOM_CONFIG mrc\n" +
                "\tLEFT JOIN SYS_BUILD_FLOOR floor ON mrc.FLOORID = floor.id \n" +
                "WHERE\n" +
                "\tmrc.ISDEL = 0  \n" +
                "GROUP BY\n" +
                "\tmrc.FLOORID  order by SORT asc  ");
        return sql.toString();
    }
}
