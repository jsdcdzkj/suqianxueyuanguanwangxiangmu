package com.jsdc.iotpt.dao;


import com.jsdc.iotpt.vo.DeviceQueryVo;
import org.springframework.stereotype.Repository;

@Repository
public class AcDailyelectricityDao {

    public String selectOneByIndoorNumber(String indoorNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (")
                .append("SELECT id, indoorNumber, createTime, totalElePower, totalPower ")
                .append("FROM Ac_Daily_ele ")
                .append("WHERE indoorNumber = '").append(indoorNumber).append("'")
                .append("ORDER BY createTime DESC ")
                .append(") WHERE ROWNUM = 1");
        return sb.toString();
    }

    public String sb(DeviceQueryVo deviceQueryVo) {
        String sql ="WITH daily_latest AS (\n" +
                "  SELECT\n" +
                "    TO_DATE(createTime, 'YYYY-MM-DD HH24:MI:SS') AS record_date,\n" +
                "    totalElePower,\n" +
                "    ROW_NUMBER() OVER (\n" +
                "      PARTITION BY TRUNC(TO_DATE(createTime, 'YYYY-MM-DD HH24:MI:SS'))\n" +
                "      ORDER BY TO_DATE(createTime, 'YYYY-MM-DD HH24:MI:SS') DESC\n" +
                "    ) AS rn\n" +
                "  FROM Ac_Daily_ele\n" +
                "  WHERE TO_DATE(createTime, 'YYYY-MM-DD HH24:MI:SS') BETWEEN TO_DATE('"+deviceQueryVo.getTimeStart()+"' , 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"+deviceQueryVo.getTimeEnd()+"', 'YYYY-MM-DD HH24:MI:SS')  AND indoorNumber = '"+deviceQueryVo.getDeviceCode()+"'" +
                ")\n" +
                "SELECT  COALESCE(SUM(totalElePower), 0) AS totalElePower\n" +
                "FROM daily_latest\n" +
                "WHERE rn = 1\n" +
                "\t"; ;
        return sql;
    }


    public String selectOneByIndoorNumberAndCreateTime(String indoorNumber,String createTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (")
                .append("SELECT id, indoorNumber, createTime, totalElePower, totalPower ")
                .append("FROM Ac_Daily_ele ")
                .append("WHERE indoorNumber = '").append(indoorNumber).append("'")
                .append(" and CREATETIME < '").append(createTime).append("'")
                .append("ORDER BY createTime DESC ")
                .append(") WHERE ROWNUM = 1");
        return sb.toString();
    }
}
