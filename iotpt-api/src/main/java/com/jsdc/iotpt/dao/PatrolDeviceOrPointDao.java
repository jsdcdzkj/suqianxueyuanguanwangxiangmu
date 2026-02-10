package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.PatrolDeviceOrPoint;
import org.springframework.stereotype.Repository;

/**
 * ClassName: PatrolDeviceOrPointDao
 * Description:
 * date: 2024/1/9 16:03
 *
 * @author bn
 */
@Repository
public class PatrolDeviceOrPointDao {



    public String getList(PatrolDeviceOrPoint patrolDeviceOrPoint){

        StringBuilder sql=new StringBuilder();

        sql.append(" select * from PATROL_DEVICE_OR_POINT pdop ");
        sql.append(" ");
        sql.append(" ");
        sql.append(" where pdop.isDel=0 and pdop.planId ="+patrolDeviceOrPoint.getPlanId());

        return sql.toString();
    }
}
