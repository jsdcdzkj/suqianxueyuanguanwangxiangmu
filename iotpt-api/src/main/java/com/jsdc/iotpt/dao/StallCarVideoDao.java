package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.CarVo;
import org.springframework.stereotype.Repository;


@Repository
public class StallCarVideoDao {
    public String getCount(){
        String sql = "SELECT \n" +
                "  (select count(1) from STALL_CAR_VIDEO where STOPTIME is not null  \n" +
                ") -\n" +
                "  (select count(1) from STALL_CAR_VIDEO where DEPARTURETIME is not null \n" +
                ") AS count\n" +
                "FROM dual" ;


        return sql ;
    }

    public String trainNumber(String time){
        String sql = "SELECT \n" +
                "    SUBSTR("+time+",0,13) AS nowTime,\n" +
                "    COUNT(*) AS count\n" +
                "FROM \n" +
                "    STALL_CAR_VIDEO\n" +
                "WHERE \n" +
                "    SUBSTR("+time+",0,13) >= TO_CHAR(TRUNC(SYSDATE), 'YYYY-MM-DD HH24')\n" +
                "    AND SUBSTR("+time+",0,13) < TO_CHAR(TRUNC(SYSDATE) + 1, 'YYYY-MM-DD HH24')\n" +
                "GROUP BY \n" +
                "    SUBSTR("+time+",0,13)\n" +
                "ORDER BY \n" +
                "    nowTime" ;
        return sql ;
    }
}
