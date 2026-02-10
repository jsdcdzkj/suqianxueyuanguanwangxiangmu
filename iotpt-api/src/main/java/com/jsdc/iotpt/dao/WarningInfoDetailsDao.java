package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.WarningInfoDetails;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

@Repository
public class WarningInfoDetailsDao {


    public String allDevice(){
        StringBuilder sql=new StringBuilder();
        sql.append("(SELECT NAME,deviceCode FROM DEVICE_COLLECT ");
        sql.append("UNION ALL SELECT NAME,deviceCode FROM DEVICE_GATEWAY ");
        sql.append("UNION ALL SELECT NAME,deviceCode FROM DEVICE_ACCESS_CONTROL ");
        sql.append("UNION ALL SELECT NAME,deviceCode FROM DEVICE_VIDEO) temp ");
        return sql.toString();
    }

    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, WarningInfoDetails bean) {
        String sql = " SELECT *  "+
                " FROM WARNING_INFO_DETAILS  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }

    public String getList(WarningInfoDetails vo){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT WI.*,SBA.AREANAME,TEMP.name deviceName FROM WARNING_INFO wi ");
        sql.append("LEFT JOIN SYS_BUILD_AREA sba ON WI.AREAID = SBA.ID ");
        sql.append("LEFT JOIN "+allDevice()+"ON WI.DEVICEID=TEMP.DEVICECODE ");
        sql.append("WHERE WI.ISDEL=0 ");
        if (null!=vo.getWarnInfoId()){
            sql.append(" AND WI.warnInfoId= "+vo.getWarnInfoId());
        }

        return sql.toString();
    }
}
