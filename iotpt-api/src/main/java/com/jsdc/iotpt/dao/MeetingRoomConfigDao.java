package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.MeetingReportVo;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingRoomConfigDao {

    /**
     * 会议室楼层分布统计
     *
     * @return
     */
    public String floorReport(MeetingReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "\tf.id,\n" +
                "\tmax( f.floorName ) name,\n" +
                "\tmax( f.SORT ),\n" +
                "\tSUM( CASE m.roomType WHEN 1 THEN 1 ELSE 0 END ) value1,\n" +
                "\tSUM( CASE m.roomType WHEN 2 THEN 1 ELSE 0 END ) value2,\n" +
                "\tSUM( CASE m.roomType WHEN 3 THEN 1 ELSE 0 END ) value3 \n" +
                "FROM\n" +
                "\tSYS_BUILD_FLOOR f\n" +
                "\tLEFT JOIN SYS_BUILD_AREA a ON f.id = a.floorId\n" +
                "\tLEFT JOIN MEETING_ROOM_CONFIG m ON m.AREAID = a.id \n" +
                "\tAND m.ISDEL = 0 ");
        sql.append("WHERE\n" +
                "\tf.ISDEL = 0 \n" +
                "GROUP BY\n" +
                "\tf.id \n" +
                "ORDER BY\n" +
                "\tmax( f.SORT ),\n" +
                "\tf.id");

        return sql.toString();
    }

    /**
     * 会议室楼层分布统计
     *
     * @return
     */
    public String deptReport(MeetingReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("\n" +
                "SELECT\n" +
                "\td.id,\n" +
                "\tmax( d.deptName ) name,\n" +
                "\tSUM( CASE m.roomType WHEN 1 THEN 1 ELSE 0 END ) value1,\n" +
                "\tSUM( CASE m.roomType WHEN 2 THEN 1 ELSE 0 END ) value2,\n" +
                "\tSUM( CASE m.roomType WHEN 3 THEN 1 ELSE 0 END ) value3 \n" +
                "FROM\n" +
                "\tsys_org_dept d\n" +
                "\tLEFT JOIN SYS_USER u ON u.deptId = d.id \n" +
                "\tAND u.ISDEL = 0\n" +
                "\tLEFT JOIN MEETING_ROOM_RESERVATION r ON r.reservationPerson = u.id \n" +
                "\tAND r.ISDEL = 0 \n");
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                sql.append(" AND r.startTime >= '").append(vo.getStartTime()).append("'");
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                sql.append(" AND r.startTime < '").append(vo.getEndTime()).append("'");
            }
        }
        sql.append("\tLEFT JOIN MEETING_ROOM_CONFIG m ON m.id = r.roomId \n" +
                "WHERE\n" +
                "\td.ISDEL = 0 \n");
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotNull(vo.getOrgId())) {
                sql.append(" and d.orgId = ").append(vo.getOrgId());
            }
        }
        sql.append(" GROUP BY\n" +
                "\td.id \n" +
                "ORDER BY\n" +
                "\td.id");

        return sql.toString();
    }

}
