package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigDeviceType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ClassName: DataSheetDao
 * Description:
 * date: 2023/5/19 11:57
 *
 * @author bn
 */
@Repository
public class DataSheetDao {

    //用电量评价
    public String energyEvaluate(Integer type) {
        String sql = " SELECT MAX(a.VAL)-MIN(a.VAL)  FROM DATA_SHEET a " +
                " where " +
                "  a.CHANNELID='temp' " ;
               switch (type){
                   case 1:
                       sql+=" AND to_char(a.TIME,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";//今天
                       break;
                   case 2:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-1) AND to_char" +
                               "(sysdate-1,'yyyy-mm-dd HH24:MI:SS')";//昨天
                       break;
                   case 3:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate,'d')+1 AND to_char" +
                               "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本周
                       break;
                   case 4:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-7,'d')+1 AND to_char" +
                               "(sysdate-7,'yyyy-mm-dd HH24:MI:SS')";//上周
                       break;
                   case 5:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate,'mm') AND to_char" +
                               "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本月
                       break;
                   case 6:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-30,'mm') AND to_char" +
                               "(sysdate-30,'yyyy-mm-dd HH24:MI:SS')";//上月
                       break;
                   case 7:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-30,'y') AND to_char" +
                               "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本年
                       break;
                   case 8:
                       sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-365,'y') AND to_char" +
                               "(sysdate-365,'yyyy-mm-dd HH24:MI:SS')";//上年
                       break;
               }
        return sql;
    }

    //用电量评价
    public String energyEvaluateByFloor(Integer type) {
        String sql = " SELECT MAX(a.VAL)-MIN(a.VAL)  as val,a.FLOORID as floor FROM DATA_SHEET a " +
                " where " +
                "  a.CHANNELID='temp' " ;
        switch (type){
            case 1:
                sql+=" AND to_char(a.TIME,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";//今天
                break;
            case 2:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-1) AND to_char" +
                        "(sysdate-1,'yyyy-mm-dd HH24:MI:SS')";//昨天
                break;
            case 3:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate,'d')+1 AND to_char" +
                        "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本周
                break;
            case 4:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-7,'d')+1 AND to_char" +
                        "(sysdate-7,'yyyy-mm-dd HH24:MI:SS')";//上周
                break;
            case 5:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate,'mm') AND to_char" +
                        "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本月
                break;
            case 6:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-30,'mm') AND to_char" +
                        "(sysdate-30,'yyyy-mm-dd HH24:MI:SS')";//上月
                break;
            case 7:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-30,'y') AND to_char" +
                        "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本年
                break;
            case 8:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-365,'y') AND to_char" +
                        "(sysdate-365,'yyyy-mm-dd HH24:MI:SS')";//上年
                break;
        }
        sql+=" GROUP BY a.FLOORID ";
        return sql;
    }


    public String getEnergyCount(){
        String sql="SELECT a.FLOORID  FROM DATA_SHEET a GROUP BY a.FLOORID";
        return sql;
    }

    //各楼层水电对比
    public String waterElectricityByFloor(Integer timeType,String type) {
        String sql = " SELECT MAX(a.VAL)-MIN(a.VAL)  as val,a.FLOORID as floor FROM DATA_SHEET a " +
                " where " +
                "  a.CHANNELID='"+type+"' " ;
        switch (timeType){
            case 1:
                sql+=" AND to_char(a.TIME,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')";//今天
                break;
            case 2:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-1) AND to_char" +
                        "(sysdate-1,'yyyy-mm-dd HH24:MI:SS')";//昨天
                break;
            case 3:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate,'d')+1 AND to_char" +
                        "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本周
                break;
            case 4:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-7,'d')+1 AND to_char" +
                        "(sysdate-7,'yyyy-mm-dd HH24:MI:SS')";//上周
                break;
            case 5:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate,'mm') AND to_char" +
                        "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本月
                break;
            case 6:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-30,'mm') AND to_char" +
                        "(sysdate-30,'yyyy-mm-dd HH24:MI:SS')";//上月
                break;
            case 7:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-30,'y') AND to_char" +
                        "(sysdate,'yyyy-mm-dd HH24:MI:SS')";//本年
                break;
            case 8:
                sql+=" AND  to_char(a.TIME,'yyyy-mm-dd HH24:MI:SS') BETWEEN trunc(sysdate-365,'y') AND to_char" +
                        "(sysdate-365,'yyyy-mm-dd HH24:MI:SS')";//上年
                break;
        }
        sql+=" GROUP BY a.FLOORID ";
        return sql;
    }


}
